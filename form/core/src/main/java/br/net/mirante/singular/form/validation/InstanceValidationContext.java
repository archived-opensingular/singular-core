package br.net.mirante.singular.form.validation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    public Map<Integer, Set<IValidationError>> getErrorsByInstanceId() {
        return getErrors().stream()
            .collect(Collectors.groupingBy(
                it -> it.getInstance().getId(),
                LinkedHashMap::new,
                Collectors.toCollection(LinkedHashSet::new)));
    }

    public void validateAll() {
        MInstances.visitAllChildrenIncludingEmpty(instance, inst -> {
            inst.getMTipo().validateInstance(new InstanceValidatable<>(inst, e -> errors.add(e)));
        });
    }
    public void validateSingle() {
        instance.getMTipo().validateInstance(new InstanceValidatable<>(instance, e -> errors.add(e)));
    }

    private static class InstanceValidatable<I extends MInstancia> implements IInstanceValidatable<I> {
        private ValidationErrorLevel            defaultLevel = ValidationErrorLevel.ERROR;
        private final I                         instance;
        private final Consumer<ValidationError> onError;
        public InstanceValidatable(I instance, Consumer<ValidationError> onError) {
            this.instance = instance;
            this.onError = onError;
        }
        @Override
        public void setDefaultLevel(ValidationErrorLevel defaultLevel) {
            this.defaultLevel = defaultLevel;
        }
        @Override
        public I getInstance() {
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
            ValidationError error = new ValidationError(instance, level, msg);
            onError.accept(error);
            return error;
        }
    }

    public boolean hasErrorsAboveLevel(ValidationErrorLevel minErrorLevel) {
        return getErrors().stream()
            .filter(it -> it.getErrorLevel().compareTo(minErrorLevel) >= 0)
            .findAny()
            .isPresent();
    }
}
