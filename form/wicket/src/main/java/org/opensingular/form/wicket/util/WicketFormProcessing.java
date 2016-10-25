/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.util;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.Visits;
import org.opensingular.form.*;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.event.ISInstanceListener;
import org.opensingular.form.event.SInstanceEvent;
import org.opensingular.form.validation.InstanceValidationContext;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.lib.commons.util.Loggable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

/*
 * TODO: depois, acho que esta classe tem que deixar de ter métodos estáticos, e se tornar algo plugável e estendível,
 *  análogo ao RequestCycle do Wicket.
 * @author ronaldtm
 */
public class WicketFormProcessing implements Loggable {

    public final static  MetaDataKey<Boolean> MDK_SKIP_VALIDATION_ON_REQUEST = new MetaDataKey<Boolean>() {
    };
    private final static MetaDataKey<Boolean> MDK_PROCESSED                  = new MetaDataKey<Boolean>() {
    };
    public final static  MetaDataKey<Boolean> MDK_FIELD_UPDATED              = new MetaDataKey<Boolean>() {
    };

    public static void onFormError(MarkupContainer container, AjaxRequestTarget target) {
        container.visitChildren((c, v) -> {
            if (c instanceof FeedbackPanel && ((FeedbackPanel) c).anyMessage()) {
                Optional.ofNullable(target).ifPresent(t -> t.add(c));
            } else if (c.hasFeedbackMessage()) {
                refreshComponentOrCellContainer(target, c);
            }
        });
    }

    public static boolean onFormSubmit(MarkupContainer container, AjaxRequestTarget target, IModel<? extends SInstance> baseInstance, boolean validate) {
        return processAndPrepareForm(container, target, baseInstance, validate);
    }

    public static boolean onFormPrepare(MarkupContainer container, IModel<? extends SInstance> baseInstance, boolean validate) {
        return processAndPrepareForm(container, null, baseInstance, validate);
    }

    private static boolean processAndPrepareForm(MarkupContainer container, AjaxRequestTarget target, IModel<? extends SInstance> baseInstanceModel, boolean validate) {

        final Function<Boolean, Boolean> setAndReturn = (value) -> {
            RequestCycle.get().setMetaData(MDK_PROCESSED, value);
            return value;
        };

        if (RequestCycle.get().getMetaData(MDK_PROCESSED) == null) {
            if (baseInstanceModel == null)
                return setAndReturn.apply(false);

            final SInstance baseInstance = baseInstanceModel.getObject();
            final SDocument document     = baseInstance.getDocument();

            // Validação do valor do componente
            boolean hasErrors = false;
            if (validate) {
                InstanceValidationContext validationContext = new InstanceValidationContext();
                validationContext.validateAll(baseInstance);
                if (validationContext.hasErrorsAboveLevel(ValidationErrorLevel.ERROR)) {
                    hasErrors = true;
                    refreshComponentOrCellContainer(target, container);
                }
            }

            updateValidationFeedbackOnDescendants(target, container);

            if (hasErrors)
                return setAndReturn.apply(false);

            // atualizar documento e recuperar instancias com atributos alterados
            document.updateAttributes(baseInstance, null);

            // re-renderizar form
            refreshComponentOrCellContainer(target, container);
        }
        return setAndReturn.apply(true);
    }

    /**
     * Forma uma chava apartir dos indexes de lista
     *
     * @param path da instancia
     * @return chaves concatenadas
     */
    private static String getIndexesKey(String path) {

        final Pattern indexFinder    = Pattern.compile("(\\[\\d\\])");
        final Pattern bracketsFinder = Pattern.compile("[\\[\\]]");

        final Matcher       matcher = indexFinder.matcher(path);
        final StringBuilder key     = new StringBuilder();

        while (matcher.find()) {
            key.append(bracketsFinder.matcher(matcher.group()).replaceAll(StringUtils.EMPTY));
        }

        return key.toString();
    }

    public static void onFieldValidate(FormComponent<?> formComponent, AjaxRequestTarget target, IModel<? extends SInstance> fieldInstance) {

        if (fieldInstance == null || fieldInstance.getObject() == null)
            return;

        if (isSkipValidationOnRequest())
            return;

        // Validação do valor do componente
        final InstanceValidationContext validationContext = new InstanceValidationContext();
        validationContext.validateSingle(fieldInstance.getObject());
        SValidationFeedbackHandler.findNearest(formComponent)
                .ifPresent(it -> it.updateValidationMessages(target));
    }

    /**
     * Executa o update listener dos tipos depentens da instancia informada, sendo chamada recursivamente para os tipos
     * que foram atualizados.
     * <p>
     * Motivação: Tendo um tipo composto com tres tipos filhos (a,b e c),
     * onde "b" é dependente de "a" e "c" é dependente de "b", "b" possui update listener que modifica o seu valor,
     * e "c" será visivel se o valor de "b" não for nulo.  Ao atualizar "a" é necessario executar o listener dos seus
     * tipos dependentes("b") e também dos tipos dependentes do seu dependente("c") para que a avaliação de visibilidade
     * seja avaliada corretamente.
     *
     * @param i instancia a ser avaliada
     * @see <a href="https://www.pivotaltracker.com/story/show/131103577">[#131103577]</a>
     */
    private static void evaluateUpdateListeners(SInstance i) {
        SInstances
                .streamDescendants(SInstances.getRootInstance(i), true)
                .filter(isDependantOf(i))
                .filter(WicketFormProcessing::isNotOrphan)
                .filter(dependant -> isNotInListOrIsBothInSameList(i, dependant))
                .forEach(dependant -> {
                    ofNullable(dependant.asAtr().getUpdateListener()).ifPresent(x -> x.accept(dependant));
                    WicketFormProcessing.evaluateUpdateListeners(dependant);
                });
    }

    private static Predicate<SInstance> isDependantOf(SInstance i) {
        return (x) -> i.getType().getDependentTypes().contains(x.getType());
    }

    private static boolean isOrphan(SInstance i) {
        return !(i instanceof SIComposite) && i.getParent() == null;
    }

    private static boolean isNotOrphan(SInstance i) {
        return !isOrphan(i);
    }

    public static void onFieldProcess(Component component, AjaxRequestTarget target, IModel<? extends SInstance> fieldInstanceModel) {

        if (fieldInstanceModel == null || fieldInstanceModel.getObject() == null) {
            return;
        }

        final SInstance                         fieldInstance  = fieldInstanceModel.getObject();
        final ISInstanceListener.EventCollector eventCollector = new ISInstanceListener.EventCollector();

        evaluateUpdateListeners(fieldInstance);
        updateAttributes(fieldInstance, eventCollector);
        validate(component, target, fieldInstance);

        if (target != null) {

            final Set<Integer> updatedInstanceIds = eventCollector.getEvents().stream()
                    .map(SInstanceEvent::getSource)
                    .map(SInstance::getId)
                    .collect(toSet());

            final Predicate<SType<?>> isDependent         = (type) -> fieldInstance.getType().isDependentType(type);
            final Predicate<SType<?>> isElementsDependent = (type) -> (type instanceof STypeList) && isDependent.test(((STypeList<?, ?>) type).getElementsType());

            final Predicate<SInstance> shouldRefreshPredicate = childInstance -> {

                if (childInstance == null) {
                    return false;
                }

                if (updatedInstanceIds.contains(childInstance.getId())) {
                    return true;
                }

                final SType<?> type = childInstance.getType();

                return (isDependent.test(type) || isElementsDependent.test(type)) && isNotInListOrIsBothInSameList(fieldInstance, childInstance);

            };

            final Predicate<SInstance> shouldntGoDepper = i -> !isParentsVisible(i);

            final Consumer<MarkupContainer> refreshDependentComponentsConsumer = rc -> rc.visitChildren(Component.class, (c, visit) -> {
                ISInstanceAwareModel.optionalCast(c.getDefaultModel()).ifPresent(model -> {
                    final SInstance ins = model.getMInstancia();
                    if (shouldntGoDepper.test(ins)) {
                        visit.dontGoDeeper();
                    } else {
                        if (shouldRefreshPredicate.test(ins)) {
                            refreshComponentOrCellContainer(target, c);
                        }
                    }
                });
            });

            // Componentes no formulario "chapado"
            WicketBuildContext
                    .findTopLevel(component)
                    .map(WicketBuildContext::getContainer)
                    .ifPresent(refreshDependentComponentsConsumer);

            final Consumer<WicketBuildContext> refreshComponentsInModalConsumer = ctx -> ctx
                    .streamParentContexts()
                    .map(WicketBuildContext::getExternalContainer)
                    .forEach(refreshDependentComponentsConsumer);

            WicketBuildContext
                    .findNearest(component)
                    .ifPresent(refreshComponentsInModalConsumer);

        }
    }

    private static boolean isNotInListOrIsBothInSameList(SInstance a, SInstance b) {
        final String pathA = pathFromList(a);
        final String pathB = pathFromList(b);
        return !(pathA != null && pathB != null && Objects.equals(pathA, pathB)) || Objects.equals(getIndexesKey(b.getPathFull()), getIndexesKey(a.getPathFull()));
    }

    private static String pathFromList(SInstance i) {
        return SInstances
                .findAncestor(i, STypeList.class)
                .map(SInstance::getPathFull)
                .orElse(null);
    }


    private static void validate(Component component, AjaxRequestTarget target, SInstance fieldInstance) {
        if (!isSkipValidationOnRequest()) {

            final InstanceValidationContext validationContext;

            // Validação do valor do componente
            validationContext = new InstanceValidationContext();
            validationContext.validateSingle(fieldInstance);

            // limpa erros de instancias dependentes, e limpa o valor caso de este não seja válido para o provider
            for (SType<?> dependentType : fieldInstance.getType().getDependentTypes()) {
                fieldInstance
                        .findNearest(dependentType)
                        .ifPresent(it -> {
                            it.getDocument().clearValidationErrors(it.getId());
                            //Executa validações que dependem do valor preenchido
                            if (!it.isEmptyOfData()) {
                                validationContext.validateSingle(it);
                            }
                        });
            }

            WicketBuildContext
                    .findNearest(component)
                    .flatMap(ctx -> Optional.of(ctx.getRootContext()))
                    .flatMap(ctx -> Optional.of(Stream.builder().add(ctx.getRootContainer()).add(ctx.getExternalContainer()).build()))
                    .ifPresent(containers -> containers.forEach(container -> {
                        updateValidationFeedbackOnDescendants(target, (MarkupContainer) container);
                    }));
        }
    }

    /**
     * Verifica se existe na hierarquia, ignora a si proprio.
     */
    private static boolean isParentsVisible(SInstance si) {
        if (si == null) {
            return false;
        }
        if (si.getParent() == null) {
            return true;
        }
        for (SInstance i = si.getParent(); i.getParent() != null; i = i.getParent()) {
            if (!(i.asAtr().isVisible() && i.asAtr().exists())) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSkipValidationOnRequest() {
        return RequestCycle.get().getMetaData(MDK_SKIP_VALIDATION_ON_REQUEST) != null && RequestCycle.get().getMetaData(MDK_SKIP_VALIDATION_ON_REQUEST);
    }

    private static void updateAttributes(final SInstance fieldInstance, ISInstanceListener.EventCollector eventCollector) {
        final SDocument document = fieldInstance.getDocument();
        document.updateAttributes(eventCollector);
    }

    private static void refreshComponentOrCellContainer(AjaxRequestTarget target, Component component) {
        if (target != null && component != null) {
            component.getRequestCycle().setMetaData(MDK_FIELD_UPDATED, true);
            target.add(WicketFormUtils.resolveRefreshingComponent(
                            ObjectUtils.defaultIfNull(
                                    WicketFormUtils.getCellContainer(component), component)));
        }
    }

    public static void updateValidationFeedbackOnDescendants(AjaxRequestTarget target,
                                                             MarkupContainer container) {

        Visits.visitPostOrder(container, (Component comp, IVisit<Void> visit) -> {
            if (SValidationFeedbackHandler.isBound(comp)) {
                SValidationFeedbackHandler.get(comp).updateValidationMessages(target);
            }
        });
    }
}
