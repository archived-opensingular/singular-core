package br.net.mirante.singular.form.wicket.util;

import static java.util.stream.Collectors.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.util.visit.Visits;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.event.IMInstanceListener;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;

public class WicketFormProcessing {

    public static boolean onFormSubmit(Form<?> form, Optional<AjaxRequestTarget> target, MInstancia instance) {
        if (instance == null)
            return false;

        // Validação do valor do componente
        InstanceValidationContext validationContext = new InstanceValidationContext(instance);
        validationContext.validateAll();
        if (validationContext.hasErrorsAboveLevel(ValidationErrorLevel.ERROR)) {
            associateErrorsToComponents(validationContext, form);
            refresh(target, form);
            return false;
        }

        // atualizar documento e recuperar instancias com atributos alterados
        instance.getDocument().updateAttributes(null);

        // re-renderizar form
        refresh(target, form);
        return true;
    }

    public static void onFieldUpdate(FormComponent<?> formComponent, Optional<AjaxRequestTarget> target, MInstancia fieldInstance) {
        if (fieldInstance == null)
            return;

        // Validação do valor do componente
        final InstanceValidationContext validationContext = new InstanceValidationContext(fieldInstance);
        validationContext.validateSingle();
        if (validationContext.hasErrorsAboveLevel(ValidationErrorLevel.ERROR)) {
            associateErrorsToComponents(validationContext, formComponent);
            refresh(target, formComponent.getParent());
            return;
        }

        // atualizar documento e recuperar os IDs das instancias com atributos alterados
        final IMInstanceListener.EventCollector eventCollector = new IMInstanceListener.EventCollector();
        fieldInstance.getDocument().updateAttributes(eventCollector);

        target.ifPresent(t -> {

            final Set<Integer> updatedInstanceIds = eventCollector.getEvents().stream()
                .map(it -> it.getSource())
                .map(it -> it.getId())
                .collect(toSet());

            final BiPredicate<Component, MInstancia> predicate = (Component c, MInstancia ins) -> {
                return (ins.getMTipo().hasProviderOpcoes() && fieldInstance.getMTipo().getDependentTypes().contains(ins.getMTipo()))
                    || (updatedInstanceIds.contains(ins.getId()));
            };

            // re-renderizar componentes
            WicketFormUtils.streamComponentsByInstance(formComponent, predicate)
                .map(c -> WicketFormUtils.findCellContainer(c))
                .filter(c -> c.isPresent())
                .map(c -> c.get())
                .filter(c -> c != null)
                .forEach(c -> t.add(c));
        });
    }

    private static void refresh(Optional<AjaxRequestTarget> target, Component component) {
        if (target.isPresent() && component != null)
            target.get()
                .add(ObjectUtils.defaultIfNull(WicketFormUtils.getCellContainer(component), component));
    }

    private static void associateErrorsToComponents(InstanceValidationContext validationContext, MarkupContainer container) {
        final Map<Integer, Set<IValidationError>> instanceErrors = validationContext.getErrorsByInstanceId();
        IVisitor<Component, Object> visitor = (component, visit) -> associateErrorsToComponent(component, instanceErrors);
        Visits.visitPostOrder(container, visitor);
        instanceErrors.values().stream()
            .forEach(it -> associateErrorsToComponent(container, it));
    }

    private static void associateErrorsToComponent(Component component, final Map<Integer, Set<IValidationError>> instanceErrors) {
        final Optional<MInstancia> instance = resolveInstance(component.getDefaultModel());
        if (!instance.isPresent())
            return;

        Set<IValidationError> errors = instanceErrors.remove(instance.get().getId());
        if (errors != null) {
            associateErrorsToComponent(component, errors);
        }
    }

    protected static void associateErrorsToComponent(Component component, Set<IValidationError> errors) {
        for (IValidationError error : errors) {
            switch (error.getErrorLevel()) {
                case ERROR:
                    component.error(error.getMessage());
                    return;
                case WARNING:
                    component.warn(error.getMessage());
                    return;
                default:
                    throw new IllegalStateException("Invalid error level: " + error.getErrorLevel());
            }
        }
    }

    protected static Optional<MInstancia> resolveInstance(final IModel<?> model) {
        //        if ((model != null) && (model.getObject() instanceof MInstancia)) {
        //            return Optional.of((MInstancia) model.getObject());
        //
        //        } else 
        if (model instanceof IMInstanciaAwareModel<?>) {
            return Optional.of(((IMInstanciaAwareModel<?>) model).getMInstancia());
        }
        return Optional.empty();
    }
}
