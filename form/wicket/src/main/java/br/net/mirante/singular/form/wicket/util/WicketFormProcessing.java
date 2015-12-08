package br.net.mirante.singular.form.wicket.util;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.event.IMInstanceListener;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;

public class WicketFormProcessing {

    public static void onFormSubmit(Form<?> form, Optional<AjaxRequestTarget> target, MInstancia instance) {
        if (instance == null)
            return;

        // Validação do valor do componente
        InstanceValidationContext validationContext = new InstanceValidationContext(instance);
        validationContext.validateAll();
        if (validationContext.hasErrorsAboveLevel(ValidationErrorLevel.ERROR)) {
            associateErrorsToComponents(validationContext, form);
            refresh(target, form);
            return;
        }

        // atualizar documento e recuperar instancias com atributos alterados
        IMInstanceListener.EventCollector eventCollector = new IMInstanceListener.EventCollector();
        instance.getDocument().updateAttributes(eventCollector);
        List<MInstancia> updatedInstances = eventCollector.getEvents().stream()
            .map(it -> it.getSource())
            .collect(toList());

        // re-renderizar componentes atualizados
        WicketFormUtils.streamComponentsByInstance(form, updatedInstances)
            .map(c -> WicketFormUtils.findCellContainer(c))
            .filter(c -> c.isPresent())
            .map(c -> c.get())
            .forEach(c -> refresh(target, c));
    }

    public static void onFieldUpdate(FormComponent<?> formComponent, Optional<AjaxRequestTarget> target, MInstancia instance) {
        if (instance == null)
            return;

        // Validação do valor do componente
        InstanceValidationContext validationContext = new InstanceValidationContext(instance);
        validationContext.validateSingle();
        if (validationContext.hasErrorsAboveLevel(ValidationErrorLevel.ERROR)) {
            associateErrorsToComponents(validationContext, formComponent);
            refresh(target, formComponent.getParent());
            return;
        }

        // atualizar documento e recuperar instancias com atributos alterados
        IMInstanceListener.EventCollector eventCollector = new IMInstanceListener.EventCollector();
        instance.getDocument().updateAttributes(eventCollector);
        List<MInstancia> updatedInstances = eventCollector.getEvents().stream()
            .map(it -> it.getSource())
            .collect(toList());

        // re-renderizar componentes atualizados
        WicketFormUtils.streamComponentsByInstance(formComponent, updatedInstances)
            .map(c -> WicketFormUtils.findCellContainer(c))
            .filter(c -> c.isPresent())
            .map(c -> c.get())
            .forEach(c -> refresh(target, c));
    }

    private static void refresh(Optional<AjaxRequestTarget> target, Component component) {
        if (target.isPresent() && component != null)
            target.get()
                .add(ObjectUtils.defaultIfNull(WicketFormUtils.getCellContainer(component), component));
    }

    private static void associateErrorsToComponents(InstanceValidationContext validationContext, MarkupContainer container) {
        final Map<Integer, Set<IValidationError>> instanceErrors = validationContext.getErrorsByInstanceId();
        container.visitChildren((component, visit) -> associateErrorsToComponent(instanceErrors, component));
    }

    private static void associateErrorsToComponent(final Map<Integer, Set<IValidationError>> instanceErrors, Component component) {
        final Optional<MInstancia> instance = resolveInstance(component.getDefaultModel());
        if (!instance.isPresent())
            return;

        Set<IValidationError> errors = instanceErrors.get(instance.get().getId());
        if (errors != null) {
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
    }

    protected static Optional<MInstancia> resolveInstance(final IModel<?> model) {
        if (model.getObject() instanceof MInstancia) {
            return Optional.of((MInstancia) model.getObject());

        } else if (model instanceof IMInstanciaAwareModel<?>) {
            return Optional.of(((IMInstanciaAwareModel<?>) model).getMInstancia());

        } else {
            return Optional.empty();
        }
    }
}
