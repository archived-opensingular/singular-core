/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.Boolean.TRUE;

public class InstanceValidationContext {

    private ListMultimap<Integer, IValidationError> contextErrors = ArrayListMultimap.create();

    public InstanceValidationContext() {}

    private ListMultimap<Integer, IValidationError> getContextErrors() {
        return contextErrors;
    }
    public Map<Integer, Collection<IValidationError>> getErrorsByInstanceId() {
        ArrayListMultimap<Integer, IValidationError> copy = ArrayListMultimap.create();
        copy.putAll(getContextErrors());
        return copy.asMap();
    }

    public void validateAll(SInstance rootInstance) {
        SInstances.visitChildren(
            rootInstance,
            (inst, v) -> validateInstance(new InstanceValidatable<>(inst, this::onError)));
        updateDocumentErrors(rootInstance);
    }
    public void validateSingle(SInstance rootInstance) {
        validateInstance(new InstanceValidatable<>(rootInstance, this::onError));
        updateDocumentErrors(rootInstance);
    }
    private void onError(IValidationError error) {
        getContextErrors().put(error.getInstanceId(), error);
    }
    protected void updateDocumentErrors(SInstance rootInstance) {
        SInstances.streamDescendants(rootInstance, true)
            .map(instance -> instance.getId())
            .forEach(instanceId -> rootInstance.getDocument()
                .setValidationErrors(instanceId, contextErrors.get(instanceId)));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <I extends SInstance> void validateInstance(IInstanceValidatable<I> validatable) {
        final I instance = validatable.getInstance();
        if (isEnabledInHierarchy(instance) && isVisibleInHierarchy(instance)) {
            if (!isFilledIfRequired(instance)) {
                validatable.error(new ValidationError(instance.getId(), ValidationErrorLevel.ERROR, "Campo obrigatório"));
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
    protected boolean isFilledIfRequired(SInstance instance) {
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
        return getContextErrors().values().stream()
            .filter(it -> it.getErrorLevel().compareTo(minErrorLevel) >= 0)
            .findAny()
            .isPresent();
    }

    private static class InstanceValidatable<I extends SInstance> implements IInstanceValidatable<I> {
        private ValidationErrorLevel             defaultLevel = ValidationErrorLevel.ERROR;
        private final I                          instance;
        private final Consumer<IValidationError> onError;
        public InstanceValidatable(I instance, Consumer<IValidationError> onError) {
            this.instance = instance;
            this.onError = onError;
        }

        private IValidationError errorInternal(ValidationErrorLevel level, String msg) {
            ValidationError error = new ValidationError(instance.getId(), level, msg);
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
