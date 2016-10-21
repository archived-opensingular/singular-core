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

package org.opensingular.form.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.SInstance;
import org.opensingular.form.SInstanceViewState;
import org.opensingular.form.SInstances;
import org.opensingular.form.SType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

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
        final List<String> pathsWithError = new ArrayList<>();
        SInstances.visitPostOrder(
                rootInstance,
                (inst, v) -> {
                    final InstanceValidatable<SInstance> validatable = new InstanceValidatable<>(inst, this::onError);
                    final boolean containsInvalidChild = pathsWithError.stream().anyMatch(s -> s.contains(inst.getPathFull()));
                    validateInstance(validatable, containsInvalidChild);
                    if (validatable.errorFound) {
                        pathsWithError.add(inst.getPathFull());
                    }
                });
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
            .map(SInstance::getId)
            .forEach(instanceId -> rootInstance.getDocument()
                .setValidationErrors(instanceId, contextErrors.get(instanceId)));
    }

    public <I extends SInstance> void validateInstance(IInstanceValidatable<I> validatable) {
        validateInstance(validatable, false);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <I extends SInstance> void validateInstance(IInstanceValidatable<I> validatable, boolean containsInvalidChild) {
        final I instance = validatable.getInstance();
        if (isEnabledInHierarchy(instance) && isVisibleInHierarchy(instance)) {
            if (!isFilledIfRequired(instance)) {
                validatable.error(new ValidationError(instance.getId(), ValidationErrorLevel.ERROR, "Campo obrigatório"));
                return;
            }
            final SType<I> tipo = (SType<I>) instance.getType();
            for (IInstanceValidator<I> validator : tipo.getValidators()) {
                if (containsInvalidChild && validator.executeOnlyIfChildrenValid())
                    continue;
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
        if (!instance.isRequired()) {
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
        public boolean                           errorFound;
        public InstanceValidatable(I instance, Consumer<IValidationError> onError) {
            this.instance = instance;
            this.onError = onError;
        }

        private IValidationError errorInternal(ValidationErrorLevel level, String msg) {
            ValidationError error = new ValidationError(instance.getId(), level, msg);
            onError.accept(error);
            errorFound = true;
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
