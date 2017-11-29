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
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.Visits;
import org.opensingular.form.*;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.event.ISInstanceListener.EventCollector;
import org.opensingular.form.validation.InstanceValidationContext;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.lib.commons.util.Loggable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/*
 * TODO: depois, acho que esta classe tem que deixar de ter métodos estáticos, e se tornar algo plugável e extensível,
 *  análogo ao RequestCycle do Wicket.
 * @author ronaldtm
 */
public class WicketFormProcessing extends SingularFormProcessing implements Loggable {

    public final static MetaDataKey<Boolean> MDK_SKIP_VALIDATION_ON_REQUEST = new MetaDataKey<Boolean>() {
    };
    private final static MetaDataKey<Boolean> MDK_PROCESSED = new MetaDataKey<Boolean>() {
    };
    public final static MetaDataKey<Boolean> MDK_FIELD_UPDATED = new MetaDataKey<Boolean>() {
    };

    private WicketFormProcessing() {
    }

    public static void onFormError(MarkupContainer container, AjaxRequestTarget target) {
        container.visitChildren((c, v) -> {
            if (c instanceof FeedbackPanel && ((FeedbackPanel) c).anyMessage()) {
                Optional.ofNullable(target).ifPresent(t -> t.add(c));
            } else if (c.hasFeedbackMessage()) {
                refreshComponentOrCellContainer(target, c);
            }
        });
    }

    public static boolean onFormSubmit(MarkupContainer container,
                                       AjaxRequestTarget target,
                                       IModel<? extends SInstance> baseInstance,
                                       boolean validate,
                                       boolean clearProcessedMetadata) {
        return processAndPrepareForm(container, target, baseInstance, validate, clearProcessedMetadata);
    }


    public static boolean onFormSubmit(MarkupContainer container, AjaxRequestTarget target, IModel<? extends SInstance> baseInstance, boolean validate) {
        return onFormSubmit(container, target, baseInstance, validate, false);
    }

    public static boolean onFormPrepare(MarkupContainer container, IModel<? extends SInstance> baseInstance, boolean validate) {
        return processAndPrepareForm(container, null, baseInstance, validate, false);
    }

    private static boolean processAndPrepareForm(MarkupContainer container,
                                                 AjaxRequestTarget target,
                                                 IModel<? extends SInstance> baseInstanceModel,
                                                 boolean validate,
                                                 boolean clearProcessedMetadata) {

        if (clearProcessedMetadata) {
            RequestCycle.get().setMetaData(MDK_PROCESSED, null);
        }

        final Function<Boolean, Boolean> setAndReturn = (value) -> {
            RequestCycle.get().setMetaData(MDK_PROCESSED, value);
            return value;
        };

        if (RequestCycle.get().getMetaData(MDK_PROCESSED) == null) {
            if (baseInstanceModel == null) {
                return setAndReturn.apply(Boolean.FALSE);
            }

            final SInstance baseInstance = baseInstanceModel.getObject();
            final SDocument document = baseInstance.getDocument();

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
                return setAndReturn.apply(Boolean.FALSE);

            // atualizar documento e recuperar instancias com atributos alterados
            document.updateAttributes(baseInstance, null);

            // re-renderizar form
            refreshComponentOrCellContainer(target, container);
        }
        return setAndReturn.apply(Boolean.TRUE);
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
     * Busca todas as instancias dependentes da instancia informada e executa o update listener.
     * Apos a execução ira procurar os componente de tela vinculados as instancias atualizadas
     * e atulizar via ajax
     *
     * @param page     a pagina
     * @param target   o ajaxtarget
     * @param instance a instancia
     */
    public static void processDependentTypes(Page page, AjaxRequestTarget target, SInstance instance) {
        updateBoundedComponents(page, target, new HashSet<>(evaluateUpdateListeners(instance)));
    }

    /**
     * Atualiza todos os componentes vinculados as instancias informadas
     */
    private static void updateBoundedComponents(Page page, AjaxRequestTarget target, Set<SInstance> instances) {
        page.visitChildren(Component.class, new SInstanceBoundedComponentUpdateVisitor(target, instances));
    }

    public static void onFieldProcess(Component component, AjaxRequestTarget target, IModel<? extends SInstance> model) {

        SInstance instance;

        if (model == null || (instance = model.getObject()) == null || target == null) {
            return;
        }

        Set<SInstance> revalidatedInstances = validate(component, target, instance);

        Set<SInstance> updatedInstances = evaluateUpdateListeners(instance);

        EventCollector eventCollector = new EventCollector();

        updateAttributes(instance, eventCollector);

        Set<SInstance> instancesToUpdateComponents = new HashSet<>();

        instancesToUpdateComponents.addAll(revalidatedInstances);
        instancesToUpdateComponents.addAll(eventCollector.getEventSourceInstances());
        instancesToUpdateComponents.addAll(updatedInstances);

        updateBoundedComponents(component.getPage(), target, instancesToUpdateComponents);
    }


    private static Set<SInstance> validate(Component component, AjaxRequestTarget target, SInstance fieldInstance) {
        Set<SInstance> revalidated = new HashSet<>();
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
                            //Executa validações que dependem do valor preenchido
                            if (it.hasValidationErrors()) {
                                it.getDocument().clearValidationErrors(it.getId());
                                validationContext.validateSingle(it);
                                revalidated.add(it);
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
        return revalidated;
    }

    private static boolean isSkipValidationOnRequest() {
        return RequestCycle.get().getMetaData(MDK_SKIP_VALIDATION_ON_REQUEST) != null && RequestCycle.get().getMetaData(MDK_SKIP_VALIDATION_ON_REQUEST);
    }

    private static void updateAttributes(final SInstance fieldInstance, EventCollector eventCollector) {
        final SDocument document = fieldInstance.getDocument();
        document.updateAttributes(eventCollector);
    }

    public static void refreshComponentOrCellContainer(AjaxRequestTarget target, Component component) {
        if (target != null && component != null) {
            component.getRequestCycle().setMetaData(MDK_FIELD_UPDATED, Boolean.TRUE);
            Component compToBeUpdated = ObjectUtils.defaultIfNull(WicketFormUtils.getCellContainer(component), component);
            target.add(WicketFormUtils.findUpdatableComponentInHierarchy(compToBeUpdated));
        }
    }

    public static void updateValidationFeedbackOnDescendants(AjaxRequestTarget target,
                                                             MarkupContainer container) {

        if (container != null) {
            Visits.visitPostOrder(container, (Component comp, IVisit<Void> visit) -> {
                if (SValidationFeedbackHandler.isBound(comp) && comp.isVisibleInHierarchy()) {
                    SValidationFeedbackHandler.get(comp).updateValidationMessages(target);
                }
            });
        }
    }
}
