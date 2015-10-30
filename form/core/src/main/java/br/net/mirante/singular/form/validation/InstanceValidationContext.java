package br.net.mirante.singular.form.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import br.net.mirante.singular.form.mform.MInstances;
import br.net.mirante.singular.form.mform.MInstancia;

public class InstanceValidationContext {

    private MInstancia             instance;
    private List<IValidationError> errors = new ArrayList<>();

    public InstanceValidationContext(MInstancia instance) {
        this.instance = instance;
    }

    public List<IValidationError> getErrors() {
        return errors;
    }

    public void validate() {
        MInstances.visitAll(instance, inst -> inst.getMTipo().validateInstance(new InstanceValidatable<>(inst, e -> errors.add(e))));
    }

    private static class InstanceValidatable<T extends MInstancia> implements IInstanceValidatable<T> {
        private ValidationErrorLevel            defaultLevel = ValidationErrorLevel.ERROR;
        private final MInstancia                instance;
        private final Consumer<ValidationError> onError;
        public InstanceValidatable(MInstancia instance, Consumer<ValidationError> onError) {
            this.instance = instance;
            this.onError = onError;
        }
        @Override
        public void setDefaultLevel(ValidationErrorLevel defaultLevel) {
            this.defaultLevel = defaultLevel;
        }
        @Override
        public MInstancia getInstance() {
            return instance;
        }
        @Override
        public void error(IValidationError error) {
            errorInternal(defaultLevel, error.getMessage());
        }
        @Override
        public IValidationError error(String msg) {
            return errorInternal(defaultLevel, msg);
        }
        @Override
        public void error(ValidationErrorLevel level, IValidationError error) {
            errorInternal(level, error.getMessage());
        }
        @Override
        public IValidationError error(ValidationErrorLevel level, String msg) {
            return errorInternal(level, msg);
        }
        private IValidationError errorInternal(ValidationErrorLevel level, String msg) {
            ValidationError error = new ValidationError(level, msg);
            onError.accept(error);
            return error;
        }
    }
}
