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

import org.opensingular.flow.core.entity.TransitionType;
import org.opensingular.flow.core.property.MetaData;
import org.opensingular.flow.core.property.MetaDataEnabled;
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
    private final TransitionType type;
    private final String         abbreviation;

    private TransitionAccessStrategy<TaskInstance> accessStrategy;
    private List<SProcessRole>                     rolesToDefineUser;

    private MetaData metaData;

    private ITransitionParametersInitializer parametersInitializer;
    private ITransitionParametersValidator   parametersValidator;

    private ITaskPredicate predicate;

    protected STransition(STask<?> origin, String name, @Nonnull STask<?> destination, @Nonnull TransitionType type) {
        this.origin = origin;
        this.name = name;
        this.destination = Objects.requireNonNull(destination);
        this.type = Objects.requireNonNull(type);
        this.abbreviation = SingularUtil.convertToJavaIdentity(name, true);
    }

    @SuppressWarnings("unchecked")
    public STransition withAccessControl(TransitionAccessStrategy<? extends TaskInstance> accessStrategy) {
        if (this.accessStrategy != null) {
            throw new SingularFlowException("Access strategy already defined");
        }
        this.accessStrategy = (TransitionAccessStrategy<TaskInstance>) accessStrategy;
        return this;
    }

    public TransitionAccess getAccessFor(TaskInstance taskInstance) {
        if (accessStrategy == null) {
            return new TransitionAccess(TransitionAccess.TransitionAccessLevel.ENABLED, null);
        }
        return accessStrategy.getAccess(taskInstance);
    }

    public boolean hasRoleUsersToSet() {
        if (rolesToDefineUser != null) {
            for (SProcessRole processRole : rolesToDefineUser) {
                if (!processRole.isAutomaticUserAllocation()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAutomaticRoleUsersToSet() {
        if (rolesToDefineUser != null) {
            return rolesToDefineUser.stream().anyMatch(SProcessRole::isAutomaticUserAllocation);
        }
        return false;
    }

    public STransition defineUserRoleInTransition(SProcessRole papel) {
        if (origin.isPeople() || papel.isAutomaticUserAllocation()) {
            if (this.rolesToDefineUser == null) {
                this.rolesToDefineUser = new ArrayList<>();
            }
            this.rolesToDefineUser.add(papel);
            return this;
        } else {
            throw new SingularFlowException(
                    "Only automatic user allocation is allowed in " + origin.getTaskType() + " tasks", origin);
        }
    }

    public List<SProcessRole> getRolesToDefine() {
        if (rolesToDefineUser == null) {
            return Collections.emptyList();
        }
        return rolesToDefineUser;
    }

    @Override
    @Nonnull
    public Optional<MetaData> getMetaDataOpt() {
        return Optional.ofNullable(metaData);
    }

    @Override
    @Nonnull
    public MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
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

    public STransition thenGo(ITaskDefinition destination) {
        return thenGo(getFlowMap().getTask(destination));
    }

    public STransition thenGo(String acao, ITaskDefinition destination) {
        return thenGo(acao, getFlowMap().getTask(destination));
    }

    public STransition thenGo(STask<?> destination) {
        return this.destination.addTransition(destination);
    }

    public STransition thenGo(String acao, STask<?> destination) {
        return this.destination.addTransition(acao, destination);
    }

    public STransition setParametersInitializer(ITransitionParametersInitializer parametersInitializer) {
        if(this.parametersInitializer != null){
            throw new SingularFlowException("Parameters Initializer already set");
        }
        this.parametersInitializer = parametersInitializer;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <K extends ProcessInstance> STransition setParametersInitializer(ITransitionParametersInitializerProcess<K> initializerByProcess) {
        return setParametersInitializer((ITransitionParametersInitializer) (params, ctx) -> initializerByProcess.init(params, (K) ctx.getProcessInstance()));
    }

    public STransition setParametersValidator(ITransitionParametersValidator parametersValidator) {
        if(this.parametersValidator != null){
            throw new SingularFlowException("Parameters Validator already set");
        }
        this.parametersValidator = parametersValidator;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <K extends ProcessInstance> STransition setParametersValidator(ITransitionParametersValidatorProcess<K> validatorByProcess) {
        return setParametersValidator((ITransitionParametersValidator) (params, result, ctx) -> validatorByProcess
            .validate(params, result, (K) ctx.getProcessInstance()));
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
    final ValidationResult validate(@Nonnull TaskInstance instancia, VarInstanceMap<?,?> parameters) {
        return validate(new RefTransition(instancia, this), parameters);
    }

    @Override
    public STransition addParamBindedToProcessVariable(String ref, boolean required) {
        super.addParamBindedToProcessVariable(ref, required);
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

    public TransitionType getType() {
        return type;
    }

    @FunctionalInterface
    public interface ITransitionParametersInitializer extends Serializable {
        void init(VarInstanceMap<?,?> params, RefTransition context);
    }

    @FunctionalInterface
    public interface ITransitionParametersInitializerProcess<K extends ProcessInstance> extends Serializable {
        void init(VarInstanceMap<?,?> params, K processInstance);
    }

    @FunctionalInterface
    public interface ITransitionParametersValidator extends Serializable {
        public void validate(VarInstanceMap<?,?> params, ValidationResult validationResult, RefTransition context);
    }

    @FunctionalInterface
    public interface ITransitionParametersValidatorProcess<K extends ProcessInstance> extends Serializable {
        void validate(VarInstanceMap<?,?> params, ValidationResult validationResult, K processInstance);
    }

}
