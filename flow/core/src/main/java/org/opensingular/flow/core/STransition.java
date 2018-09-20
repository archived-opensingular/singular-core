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

package org.opensingular.flow.core;

import org.opensingular.flow.core.property.MetaDataEnabled;
import org.opensingular.flow.core.property.MetaDataMap;
import org.opensingular.flow.core.variable.ValidationResult;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.lib.commons.base.SingularUtil;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class STransition extends SParametersEnabled implements MetaDataEnabled {

    private final STask<?> origin;
    private final String name;
    private final STask<?> destination;
    private final String abbreviation;

    private UITransitionAccessStrategy<TaskInstance> accessStrategy;
    private List<SBusinessRole> rolesToDefineUser;

    private MetaDataMap metaDataMap;

    private ITransitionParametersInitializer parametersInitializer;
    private ITransitionParametersValidator   parametersValidator;

    private ITaskPredicate predicate;
    private DisplayInfoTransition displayInfo;

    protected STransition(@Nonnull STask<?> origin, @Nonnull String name, @Nonnull STask<?> destination) {
        this.origin = Objects.requireNonNull(origin);
        this.name = Objects.requireNonNull(name);
        this.destination = Objects.requireNonNull(destination);
        this.abbreviation = SingularUtil.convertToJavaIdentifier(name);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public STransition withAccessControl(UITransitionAccessStrategy<? extends TaskInstance> accessStrategy) {
        if (this.accessStrategy != null) {
            throw new SingularFlowException("Access strategy already defined");
        }
        this.accessStrategy = (UITransitionAccessStrategy<TaskInstance>) inject(accessStrategy);
        return this;
    }

    public TransitionAccess getAccessFor(TaskInstance taskInstance) {
        if (accessStrategy == null) {
            return new TransitionAccess(TransitionAccess.TransitionVisibilityLevel.ENABLED_AND_VISIBLE, null);
        }
        return accessStrategy.getAccess(taskInstance);
    }

    public boolean hasRoleUsersToSet() {
        if (rolesToDefineUser != null) {
            for (SBusinessRole businessRole : rolesToDefineUser) {
                if (!businessRole.isAutomaticBusinessRoleAllocation()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAutomaticRoleUsersToSet() {
        if (rolesToDefineUser != null) {
            return rolesToDefineUser.stream().anyMatch(SBusinessRole::isAutomaticBusinessRoleAllocation);
        }
        return false;
    }

    public STransition defineBusinessRoleInTransition(SBusinessRole role) {
        if (origin.isPeople() || role.isAutomaticBusinessRoleAllocation()) {
            if (this.rolesToDefineUser == null) {
                this.rolesToDefineUser = new ArrayList<>();
            }
            this.rolesToDefineUser.add(role);
            return this;
        } else {
            throw new SingularFlowException(
                    "Only automatic user allocation is allowed in " + origin.getTaskType() + " tasks", origin);
        }
    }

    public List<SBusinessRole> getRolesToDefine() {
        if (rolesToDefineUser == null) {
            return Collections.emptyList();
        }
        return rolesToDefineUser;
    }

    @Override
    @Nonnull
    public Optional<MetaDataMap> getMetaDataOpt() {
        return Optional.ofNullable(metaDataMap);
    }

    @Override
    @Nonnull
    public MetaDataMap getMetaData() {
        if (metaDataMap == null) {
            metaDataMap = new MetaDataMap();
        }
        return metaDataMap;
    }

    public STask<?> getOrigin() {
        return origin;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public STask<?> getDestination() {
        return destination;
    }

    public STransition thenGo(@Nonnull ITaskDefinition destination) {
        return thenGo(getFlowMap().getTask(destination));
    }

    public STransition thenGo(String actionName, ITaskDefinition destination) {
        return thenGo(actionName, getFlowMap().getTask(destination));
    }

    public STransition thenGo(@Nonnull STask<?> destination) {
        return this.destination.addTransition(destination);
    }

    public STransition thenGo(String actionName, STask<?> destination) {
        return this.destination.addTransition(actionName, destination);
    }

    @Nonnull
    public STransition setParametersInitializer(@Nonnull ITransitionParametersInitializer parametersInitializer) {
        if(this.parametersInitializer != null){
            throw new SingularFlowException("Parameters Initializer already set");
        }
        this.parametersInitializer = inject(parametersInitializer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <K extends FlowInstance> STransition setParametersInitializer(
            @Nonnull ITransitionParametersInitializerWithFlowInstance<K> initializer) {
        inject(initializer);
        return setParametersInitializer((ITransitionParametersInitializer) (params, ctx) -> initializer.init(params, (K) ctx.getFlowInstance()));
    }

    @Nonnull
    public STransition setParametersValidator(@Nonnull ITransitionParametersValidator parametersValidator) {
        if(this.parametersValidator != null){
            throw new SingularFlowException("Parameters Validator already set");
        }
        this.parametersValidator = inject(parametersValidator);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <K extends FlowInstance> STransition setParametersValidator(
            @Nonnull ITransitionParametersValidatorWithFlowInstance<K> validator) {
        inject(validator);
        return setParametersValidator((ITransitionParametersValidator) (params, result, ctx) -> validator
            .validate(params, result, (K) ctx.getFlowInstance()));
    }

    @Nonnull
    final VarInstanceMap<?,?> newTransitionParameters(@Nonnull RefTransition refTransition) {
        Objects.requireNonNull(refTransition);
        VarInstanceMap<?,?> params = getParameters().newInstanceMap();
        if (parametersInitializer != null) {
            parametersInitializer.init(params, refTransition);
        }
        return params;
    }

    @Nonnull
    final ValidationResult validate(@Nonnull RefTransition refTransition, VarInstanceMap<?,?> parameters) {
        ValidationResult validationResult = new ValidationResult();
        if (parametersValidator != null) {
            parametersValidator.validate(parameters, validationResult, refTransition);
        }
        return validationResult;
    }

    @Nonnull
    final ValidationResult validate(@Nonnull TaskInstance instance, VarInstanceMap<?,?> parameters) {
        return validate(new RefTransition(instance, this), parameters);
    }

    @Override
    public STransition addParamBindedToFlowVariable(String ref, boolean required) {
        super.addParamBindedToFlowVariable(ref, required);
        return this;
    }

    final void setPredicate(ITaskPredicate predicate) {
        this.predicate = predicate;
    }

    public ITaskPredicate getPredicate() {
        return predicate;
    }


    @Override
    FlowMap getFlowMap() {
        return destination.getFlowMap();
    }

    @Override
    public String toString() {
        return name + "(go to task:" + destination.getName() + ")";
    }

    /**
     * Returns the display information of the transition that may be used to help the diagram generation of the flow.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    @Nonnull
    public DisplayInfoTransition getDisplayInfo() {
        if (displayInfo == null) {
            displayInfo = new DisplayInfoTransition(this);
        }
        return displayInfo;
    }

    @FunctionalInterface
    public interface ITransitionParametersInitializer extends Serializable {
        void init(VarInstanceMap<?,?> params, RefTransition context);
    }

    @FunctionalInterface
    public interface ITransitionParametersInitializerWithFlowInstance<K extends FlowInstance> extends Serializable {
        void init(VarInstanceMap<?,?> params, K flowInstance);
    }

    @FunctionalInterface
    public interface ITransitionParametersValidator extends Serializable {
        public void validate(VarInstanceMap<?,?> params, ValidationResult validationResult, RefTransition context);
    }

    @FunctionalInterface
    public interface ITransitionParametersValidatorWithFlowInstance<K extends FlowInstance> extends Serializable {
        void validate(VarInstanceMap<?,?> params, ValidationResult validationResult, K flowInstance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STransition that = (STransition) o;
        return Objects.equals(origin, that.origin) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = origin.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
