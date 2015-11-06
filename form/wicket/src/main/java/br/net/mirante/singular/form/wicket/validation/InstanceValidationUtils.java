package br.net.mirante.singular.form.wicket.validation;

import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;

public class InstanceValidationUtils {

    public static void associateErrorsToComponents(InstanceValidationContext validationContext, MarkupContainer container) {

        validationContext.validate();

        Map<MInstancia, List<IValidationError>> instanceErrors = validationContext.getErrorsByInstance();

        container.visitChildren((Component component, IVisit<Void> visit) -> {

            final IModel<?> model = component.getDefaultModel();

            if (model instanceof IMInstanciaAwareModel<?>) {
                MInstancia instance = ((IMInstanciaAwareModel<?>) model).getMInstancia();
                if (instanceErrors.containsKey(instance)) {
                    for (IValidationError error : instanceErrors.get(instance)) {
                        ValidationErrorLevel errorLevel = error.getErrorLevel();
                        switch (errorLevel) {
                            case ERROR:
                                component.error(error.getMessage());
                                break;
                            case WARNING:
                                component.warn(error.getMessage());
                                break;
                            default:
                                throw new IllegalStateException("Invalid error level: " + errorLevel);
                        }
                    }
                }
            }
        });
    }
}
