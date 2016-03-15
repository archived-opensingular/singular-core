/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.validation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.SInstanceViewState;
import br.net.mirante.singular.form.mform.SInstances;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.core.SPackageCore;

public class InstanceValidationContext {

    private SInstance rootInstance;
    private List<IValidationError> errors = new ArrayList<>();

    public InstanceValidationContext(SInstance instance) {
        this.rootInstance = instance;
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
        SInstances.visitAllChildrenIncludingEmpty(rootInstance, inst -> validateInstance(new InstanceValidatable<>(inst, errors::add)));
    }
    public void validateSingle() {
        validateInstance(new InstanceValidatable<>(rootInstance, errors::add));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <I extends SInstance> void validateInstance(IInstanceValidatable<I> validatable) {
        final I instance = validatable.getInstance();

        if (isEnabledInHierarchy(instance) && isVisibleInHierarchy(instance) && !checkRequired(instance, true)) {
            validatable.error(new ValidationError(instance, ValidationErrorLevel.ERROR, "Campo obrigatório"));
            return;
        }

        final SType<I> tipo = (SType<I>) instance.getType();
        for (IInstanceValidator<I> validator : tipo.getValidators()) {
            validatable.setDefaultLevel(tipo.getValidatorErrorLevel(validator));
            validator.validate((IInstanceValidatable) validatable);
        }
    }

    protected boolean checkRequired(SInstance instance, boolean ignoreDisabledAndInvisible) {
        if (!Boolean.TRUE.equals(instance.getAttributeValue(SPackageCore.ATR_REQUIRED)))
            return true;

        if (instance instanceof ICompositeInstance) {
            ICompositeInstance comp = (ICompositeInstance) instance;
            return comp.streamDescendants(false).anyMatch(it -> it.getValue() != null);

        } else {
            return (instance.getValue() != null);
        }
    }

    protected boolean isEnabledInHierarchy(SInstance instance) {
        return !SInstances.listAscendants(instance, true).stream()
                .map(it -> SInstanceViewState.get(it).isEnabled())
                .anyMatch(Boolean.FALSE::equals);
    }

    protected boolean isVisibleInHierarchy(SInstance instance) {
        return !SInstances.listAscendants(instance, true).stream()
                .map(it -> SInstanceViewState.get(it).isVisible())
                .anyMatch(Boolean.FALSE::equals);
    }

    public boolean hasErrorsAboveLevel(ValidationErrorLevel minErrorLevel) {
        return getErrors().stream()
            .filter(it -> it.getErrorLevel().compareTo(minErrorLevel) >= 0)
            .findAny()
            .isPresent();
    }

    private static class InstanceValidatable<I extends SInstance> implements IInstanceValidatable<I> {
        private ValidationErrorLevel            defaultLevel = ValidationErrorLevel.ERROR;
        private final I                         instance;
        private final Consumer<ValidationError> onError;
        public InstanceValidatable(I instance, Consumer<ValidationError> onError) {
            this.instance = instance;
            this.onError = onError;
        }

        private IValidationError errorInternal(ValidationErrorLevel level, String msg) {
            ValidationError error = new ValidationError(instance, level, msg);
            onError.accept(error);
            return error;
        }

        //@formatter:off
        @Override public I getInstance()                                                            { return instance; }
        @Override public ValidationErrorLevel    getDefaultLevel()                                  { return defaultLevel; }
        @Override public IInstanceValidatable<I> setDefaultLevel(ValidationErrorLevel defaultLevel) { this.defaultLevel = defaultLevel; return this; }
        @Override public IValidationError error(                            String msg)             { return errorInternal(defaultLevel, msg); }
        @Override public IValidationError error(                            IValidationError error) { return errorInternal(defaultLevel, error.getMessage()); }
        @Override public IValidationError error(ValidationErrorLevel level, String msg)             { return errorInternal(level, msg); }
        @Override public IValidationError error(ValidationErrorLevel level, IValidationError error) { return errorInternal(level, error.getMessage()); }
        //@formatter:on
    }
}
