/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

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
        if (isEnabledInHierarchy(instance) && isVisibleInHierarchy(instance)) {
            if (!checkIfIsRequiredAndIfIsFilled(instance)) {
                validatable.error(new ValidationError(instance, ValidationErrorLevel.ERROR, "Campo obrigatório"));
                return;
            }
            final SType<I> tipo = (SType<I>) instance.getType();
            for (IInstanceValidator<I> validator : tipo.getValidators()) {
                validatable.setDefaultLevel(tipo.getValidatorErrorLevel(validator));
                validator.validate((IInstanceValidatable) validatable);
            }
        }
    }

    /**
     * Chea se a instancia é obrigatoria e se a mesma foi preenchida
     * @param instance
     * @return true se estiver OK
     */
    protected boolean checkIfIsRequiredAndIfIsFilled(SInstance instance) {
        if (!TRUE.equals(instance.getAttributeValue(SPackageBasic.ATR_REQUIRED))) {
            return true;
        }
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
