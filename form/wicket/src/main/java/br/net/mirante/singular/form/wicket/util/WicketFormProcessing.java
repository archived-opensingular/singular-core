package br.net.mirante.singular.form.wicket.util;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.Visits;

import br.net.mirante.singular.form.mform.MFormUtil;
import br.net.mirante.singular.form.mform.MInstances;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.event.IMInstanceListener;
import br.net.mirante.singular.form.mform.event.SInstanceEvent;
import br.net.mirante.singular.form.mform.options.MSelectionableType;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.feedback.SFeedbackMessage;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;
import static java.util.stream.Collectors.toSet;

/*
 * TODO: depois, acho que esta classe tem que deixar de ter métodos estáticos, e se tornar algo plugável e estendível,
 *  análogo ao RequestCycle do Wicket.
 * @author ronaldtm
 */
public class WicketFormProcessing {

    public static void onFormError(MarkupContainer container, Optional<AjaxRequestTarget> target, IModel<? extends SInstance> baseInstance) {
        container.visitChildren((c, v) -> {
            if (c instanceof FeedbackPanel && ((FeedbackPanel) c).anyMessage())
                target.ifPresent(t -> t.add(c));
            else if (c.hasFeedbackMessage())
                refresh(target, c);
        });
    }

    public static boolean onFormSubmit(MarkupContainer container, Optional<AjaxRequestTarget> target, IModel<? extends SInstance> baseInstance, boolean validate) {
        if (baseInstance == null)
            return false;

        // Validação do valor do componente
        if (validate) {
            InstanceValidationContext validationContext = new InstanceValidationContext(baseInstance.getObject());
            validationContext.validateAll();
            if (validationContext.hasErrorsAboveLevel(ValidationErrorLevel.ERROR)) {
                associateErrorsToComponents(validationContext, container, baseInstance);
                refresh(target, container);
                return false;
            }
        }

        // atualizar documento e recuperar instancias com atributos alterados
        baseInstance.getObject().getDocument().updateAttributes(null);

        // re-renderizar form
        refresh(target, container);
        return true;
    }

    public static void onFieldUpdate(FormComponent<?> formComponent, Optional<AjaxRequestTarget> target, IModel<? extends SInstance> fieldInstance) {
        if (fieldInstance == null || fieldInstance.getObject() == null)
            return;

        // Validação do valor do componente
        final InstanceValidationContext validationContext = new InstanceValidationContext(fieldInstance.getObject());
        validationContext.validateSingle();
        if (validationContext.hasErrorsAboveLevel(ValidationErrorLevel.ERROR)) {
            associateErrorsToComponents(validationContext, formComponent, fieldInstance);
            refresh(target, formComponent.getParent());
            return;
        }

        // atualizar documento e recuperar os IDs das instancias com atributos alterados
        final IMInstanceListener.EventCollector eventCollector = new IMInstanceListener.EventCollector();
        fieldInstance.getObject().getDocument().updateAttributes(eventCollector);

        refresh(target, formComponent);
        target.ifPresent(t -> {

            final Set<Integer> updatedInstanceIds = eventCollector.getEvents().stream()
                    .map(SInstanceEvent::getSource)
                    .map(SInstance::getId)
                    .collect(toSet());

            final BiPredicate<Component, SInstance> predicate = (Component c, SInstance ins) -> {
                SType<?> insTipo = ins.getMTipo();
                boolean wasUpdated = updatedInstanceIds.contains(ins.getId());
                boolean hasOptions = (insTipo instanceof MSelectionableType<?>) && ((MSelectionableType<?>) insTipo).hasProviderOpcoes();
                boolean dependsOnField = fieldInstance.getObject().getMTipo().getDependentTypes().contains(insTipo);
                return wasUpdated || (hasOptions && dependsOnField);
            };

            //re-renderizar componentes
            formComponent.getPage().visitChildren(Component.class, (c, visit) -> {
                if (c.getDefaultModel() != null && IMInstanciaAwareModel.class.isAssignableFrom(c.getDefaultModel().getClass())) {
                    IMInstanciaAwareModel model = (IMInstanciaAwareModel) c.getDefaultModel();
                    if (predicate.test(c, model.getMInstancia())) {
                        (model).getMInstancia().clearInstance();
                        t.add(c);
                    }
                }
            });

//           // re-renderizar componentes
//            WicketFormUtils.streamComponentsByInstance(formComponent, predicate)
//                    .map(WicketFormUtils::findCellContainer)
//                    .filter(Optional::isPresent)
//                    .map(Optional::get)
//                    .filter(c -> c != null)
//                    .forEach(t::add);
        });
    }


    private static void refresh(Optional<AjaxRequestTarget> target, Component component) {
        if (target.isPresent() && component != null)
            target.get()
                    .add(ObjectUtils.defaultIfNull(WicketFormUtils.getCellContainer(component), component));
    }

    public static void associateErrorsToComponents(InstanceValidationContext validationContext, MarkupContainer container, IModel<? extends SInstance> baseInstance) {
        final Map<Integer, Set<IValidationError>> instanceErrors = validationContext.getErrorsByInstanceId();

        // associate errors to components
        Visits.visitPostOrder(container, (Component component, IVisit<Object> visit) -> {
            if (!component.isVisibleInHierarchy()) {
                visit.dontGoDeeper();
            } else {
                WicketFormUtils.resolveInstance(component.getDefaultModel())
                        .map(componentInstance -> instanceErrors.remove(componentInstance.getId()))
                        .ifPresent(errors -> associateErrorsTo(component, baseInstance, false, errors));
            }
        });

        // associate remaining errors to container
        instanceErrors.values().stream()
                .forEach(it -> associateErrorsTo(container, baseInstance, true, it));
    }

    private static void associateErrorsTo(Component component, IModel<? extends SInstance> baseInstance,
                                          boolean prependFullPathLabel, Set<IValidationError> errors) {
        for (IValidationError error : errors) {
            String message = error.getMessage();
            if (prependFullPathLabel) {
                final String labelPath = MFormUtil.generateUserFriendlyPath(error.getInstance(), baseInstance.getObject());
                if (StringUtils.isNotBlank(labelPath))
                    message = labelPath + " : " + message;
            }
            Integer instanceId = error.getInstance().getId();

            final IModel<? extends SInstance> instanceModel = (IReadOnlyModel<SInstance>) () ->
                    MInstances.streamDescendants(baseInstance.getObject().getDocument().getRoot(), true)
                            .filter(it -> Objects.equals(it.getId(), instanceId))
                            .findFirst()
                            .orElse(null);

            final FeedbackMessages feedbackMessages = component.getFeedbackMessages();

            if (error.getErrorLevel() == ValidationErrorLevel.ERROR)
                feedbackMessages.add(new SFeedbackMessage(component, message, FeedbackMessage.ERROR, instanceModel));

            else if (error.getErrorLevel() == ValidationErrorLevel.WARNING)
                feedbackMessages.add(new SFeedbackMessage(component, message, FeedbackMessage.WARNING, instanceModel));

            else
                throw new IllegalStateException("Invalid error level: " + error.getErrorLevel());
        }
    }
}
