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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.SInstance;
import org.opensingular.form.SInstanceViewState;
import org.opensingular.form.SInstances;
import org.opensingular.form.SType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class InstanceValidationContext {

    private final ListMultimap<Integer, ValidationError> contextErrors = ArrayListMultimap.create();

    public InstanceValidationContext() {
    }

    private ListMultimap<Integer, ValidationError> getContextErrors() {
        return contextErrors;
    }

    public Map<Integer, Collection<ValidationError>> getErrorsByInstanceId() {
        final ArrayListMultimap<Integer, ValidationError> copy = ArrayListMultimap.create();
        copy.putAll(getContextErrors());
        return copy.asMap();
    }

    public void validateAll(SInstance rootInstance) {
        final List<String> pathsWithError = new ArrayList<>();
        rootInstance.getDocument().updateAttributes(rootInstance, null);
        SInstances.visitPostOrder(rootInstance, (inst, v) -> {
            final InstanceValidatableImpl<?> validatable = new InstanceValidatableImpl<>(inst, this::onError);
            final boolean containsInvalidChild = pathsWithError.stream().anyMatch(s -> s.contains(inst.getPathFull()));
            validateInstance(validatable, containsInvalidChild);
            if (validatable.errorFound) {
                pathsWithError.add(inst.getPathFull());
            }
        });
        updateDocumentErrors(rootInstance);
    }

    public void validateSingle(SInstance rootInstance) {
        validateInstance(new InstanceValidatableImpl<>(rootInstance, this::onError));
        updateDocumentErrors(rootInstance);
    }

    private void onError(ValidationError error) {
        getContextErrors().put(error.getInstanceId(), error);
    }

    protected void updateDocumentErrors(SInstance rootInstance) {
        SInstances.streamDescendants(rootInstance, true)
                .forEach(instance -> {
                    Integer id = instance.getId();
                    rootInstance.getDocument()
                            .setValidationErrors(id, contextErrors.get(id));
                });
    }

    public <I extends SInstance> void validateInstance(InstanceValidatable<I> validatable) {
        validateInstance(validatable, false);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected <I extends SInstance> void validateInstance(InstanceValidatable<I> validatable, boolean containsInvalidChild) {
        final I instance = validatable.getInstance();
        if (isEnabledInHierarchy(instance) && isVisibleInHierarchy(instance)) {
            if (!isFilledIfRequired(instance)) {
                validatable.error(new ValidationErrorImpl(instance.getId(), ValidationErrorLevel.ERROR, "Campo obrigatório"));
                return;
            }
            final SType<I> type = (SType<I>) instance.getType();
            for (ValidationEntry<I> entry : type.getValidators()) {
                if (containsInvalidChild && entry.getValidator().executeOnlyIfChildrenValid())
                    continue;
                validatable.setDefaultLevel(entry.getErrorLevel());
                entry.getValidator().validate(validatable);
            }
        }
    }

    /**
     * Checa se a instancia é obrigatoria e se a mesma foi preenchida
     *
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
        return SInstances.listAscendants(instance, true).stream()
                .map(it -> SInstanceViewState.get(it).isEnabled())
                .noneMatch(Boolean.FALSE::equals);
    }

    protected boolean isVisibleInHierarchy(SInstance instance) {
        return SInstances.listAscendants(instance, true).stream()
                .map(it -> SInstanceViewState.get(it).isVisible())
                .noneMatch(Boolean.FALSE::equals);
    }

    public boolean hasErrorsAboveLevel(ValidationErrorLevel minErrorLevel) {
        return getContextErrors().values().stream()
                .anyMatch(it -> it.getErrorLevel().compareTo(minErrorLevel) >= 0);
    }

    private static class InstanceValidatableImpl<I extends SInstance> implements InstanceValidatable<I> {

        private ValidationErrorLevel defaultLevel = ValidationErrorLevel.ERROR;
        private final I instance;
        private final Consumer<ValidationError> onError;
        public boolean errorFound;

        public InstanceValidatableImpl(I instance, Consumer<ValidationError> onError) {
            this.instance = instance;
            this.onError = onError;
        }

        private ValidationError errorInternal(ValidationErrorLevel level, String msg) {
            ValidationError error = new ValidationErrorImpl(instance.getId(), level, msg);
            onError.accept(error);
            errorFound = true;
            return error;
        }

        @Override
        public I getInstance() {
            return instance;
        }

        @Override
        public ValidationErrorLevel getDefaultLevel() {
            return defaultLevel;
        }

        @Override
        public InstanceValidatable<I> setDefaultLevel(ValidationErrorLevel defaultLevel) {
            this.defaultLevel = defaultLevel;
            return this;
        }

        @Override
        public ValidationError error(String msg) {
            return errorInternal(defaultLevel, msg);
        }

        @Override
        public ValidationError error(ValidationError error) {
            return errorInternal(defaultLevel, error.getMessage());
        }

        @Override
        public ValidationError error(ValidationErrorLevel level, String msg) {
            return errorInternal(level, msg);
        }

        @Override
        public ValidationError error(ValidationErrorLevel level, ValidationError error) {
            return errorInternal(level, error.getMessage());
        }
    }
}
