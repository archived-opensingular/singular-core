/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.util;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SInstances;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.event.ISInstanceListener;
import br.net.mirante.singular.form.event.SInstanceEvent;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.SValidationFeedbackHandler;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.ISInstanceAwareModel;
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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;

/*
 * TODO: depois, acho que esta classe tem que deixar de ter métodos estáticos, e se tornar algo plugável e estendível,
 *  análogo ao RequestCycle do Wicket.
 * @author ronaldtm
 */
public class WicketFormProcessing implements Loggable {

    public final static MetaDataKey<Boolean> MDK_SKIP_VALIDATION_ON_REQUEST = new MetaDataKey<Boolean>() {
    };
    public final static MetaDataKey<Boolean> MDK_PROCESSED                  = new MetaDataKey<Boolean>() {
    };
    public final static MetaDataKey<Boolean> MDK_FIELD_UPDATED              = new MetaDataKey<Boolean>() {
    };

    public static void onFormError(MarkupContainer container, Optional<AjaxRequestTarget> target, IModel<? extends SInstance> baseInstance) {
        container.visitChildren((c, v) -> {
            if (c instanceof FeedbackPanel && ((FeedbackPanel) c).anyMessage())
                target.ifPresent(t -> t.add(c));
            else if (c.hasFeedbackMessage())
                refreshComponentOrCellContainer(target, c);
        });
    }

    public static boolean onFormSubmit(MarkupContainer container, Optional<AjaxRequestTarget> target, IModel<? extends SInstance> baseInstance, boolean validate) {
        return processAndPrepareForm(container, target, baseInstance, validate);
    }

    public static boolean onFormPrepare(MarkupContainer container, IModel<? extends SInstance> baseInstance, boolean validate) {
        return processAndPrepareForm(container, Optional.empty(), baseInstance, validate);
    }

    private static boolean processAndPrepareForm(MarkupContainer container, Optional<AjaxRequestTarget> target, IModel<? extends SInstance> baseInstanceModel, boolean validate) {

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

            updateValidationFeedbackOnDescendants(
                    target,
                    container,
                    baseInstanceModel,
                    document.getValidationErrorsByInstanceId());

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
    protected static String getIndexesKey(String path) {

        final Pattern indexFinder    = Pattern.compile("(\\[\\d\\])");
        final Pattern bracketsFinder = Pattern.compile("[\\[\\]]");

        final Matcher       matcher = indexFinder.matcher(path);
        final StringBuilder key     = new StringBuilder();

        while (matcher.find()) {
            key.append(bracketsFinder.matcher(matcher.group()).replaceAll(StringUtils.EMPTY));
        }

        return key.toString();
    }

    public static void onFieldValidate(FormComponent<?> formComponent, Optional<AjaxRequestTarget> target, IModel<? extends SInstance> fieldInstance) {

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
        Optional.ofNullable(i.asAtr().getUpdateListener()).ifPresent(x -> x.accept(i));
        SInstances.streamDescendants(SInstances.getRootInstance(i), true)
                .filter(a -> i.getType().getDependentTypes().contains(a.getType()))
                .forEach(WicketFormProcessing::evaluateUpdateListeners);
    }

    public static void onFieldProcess(FormComponent<?> formComponent, Optional<AjaxRequestTarget> target, IModel<? extends SInstance> fieldInstanceModel) {

        if (fieldInstanceModel == null || fieldInstanceModel.getObject() == null) {
            return;
        }

        final SInstance fieldInstance = fieldInstanceModel.getObject();

        evaluateUpdateListeners(fieldInstance);

        ISInstanceListener.EventCollector eventCollector = new ISInstanceListener.EventCollector();
        updateAttributes(fieldInstance, eventCollector);

        if (!isSkipValidationOnRequest()) {

            // Validação do valor do componente
            final InstanceValidationContext validationContext = new InstanceValidationContext();
            validationContext.validateSingle(fieldInstance);

            // limpa erros de instancias dependentes, e limpa o valor caso de este não seja válido para o provider
            for (SType<?> dependentType : fieldInstance.getType().getDependentTypes()) {
                fieldInstance.findNearest(dependentType)
                        .ifPresent(it -> it.getDocument().clearValidationErrors(it.getId()));
            }

            WicketBuildContext
                    .findNearest(formComponent)
                    .map(WicketBuildContext::getRootContainer)
                    .ifPresent(nearestContainer -> {
                        updateValidationFeedbackOnDescendants(
                                target,
                                nearestContainer,
                                fieldInstanceModel,
                                validationContext.getErrorsByInstanceId());
                    });
        }

        if (target.isPresent()) {

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

                if (isDependent.test(type) || isElementsDependent.test(type)) {
                    final Function<SInstance, String> pathFull = inst -> SInstances
                            .findAncestor(inst, STypeList.class)
                            .map(SInstance::getPathFull)
                            .orElse(null);
                    final boolean bothInList = Objects.equals(pathFull.apply(childInstance), pathFull.apply(fieldInstance));
                    return !bothInList || Objects.equals(getIndexesKey(childInstance.getPathFull()), getIndexesKey(fieldInstance.getPathFull()));
                }

                return false;
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
                    .findTopLevel(formComponent)
                    .map(WicketBuildContext::getContainer)
                    .ifPresent(refreshDependentComponentsConsumer);

            final Consumer<WicketBuildContext> refreshComponentsInModalConsumer = ctx -> ctx
                    .streamParentContexts()
                    .map(WicketBuildContext::getExternalContainer)
                    .forEach(refreshDependentComponentsConsumer);

            WicketBuildContext
                    .findNearest(formComponent)
                    .ifPresent(refreshComponentsInModalConsumer);

        }
    }

    /**
     * Verifica se existe na hierarquia, ignora a si proprio.
     */
    public static boolean isParentsVisible(SInstance si) {
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

    protected static boolean isSkipValidationOnRequest() {
        return RequestCycle.get().getMetaData(MDK_SKIP_VALIDATION_ON_REQUEST) != null && RequestCycle.get().getMetaData(MDK_SKIP_VALIDATION_ON_REQUEST);
    }

    private static void updateAttributes(final SInstance fieldInstance, ISInstanceListener.EventCollector eventCollector) {
        final SDocument document = fieldInstance.getDocument();
        document.updateAttributes(eventCollector);
    }

    private static void refreshComponentOrCellContainer(Optional<AjaxRequestTarget> target, Component component) {
        if (target.isPresent() && component != null) {
            component.getRequestCycle().setMetaData(MDK_FIELD_UPDATED, true);
            target.get()
                    .add(WicketFormUtils.resolveRefreshingComponent(
                            ObjectUtils.defaultIfNull(
                                    WicketFormUtils.getCellContainer(component), component)));
        }
    }

    public static void updateValidationFeedbackOnDescendants(Optional<AjaxRequestTarget> target,
                                                             MarkupContainer container,
                                                             IModel<? extends SInstance> baseInstance,
                                                             Map<Integer, ? extends Collection<IValidationError>> instanceErrors) {

        Visits.visitPostOrder(container, (Component comp, IVisit<Void> visit) -> {
            if (SValidationFeedbackHandler.isBound(comp))
                SValidationFeedbackHandler.get(comp).updateValidationMessages(target);
        });
    }
}
