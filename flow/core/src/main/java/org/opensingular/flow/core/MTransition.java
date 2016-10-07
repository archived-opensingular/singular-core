/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import org.opensingular.flow.core.builder.ITaskDefinition;
import org.opensingular.flow.core.entity.TransitionType;
import org.opensingular.flow.core.property.MetaData;
import org.opensingular.flow.core.property.MetaDataRef;
import org.opensingular.flow.core.variable.ValidationResult;
import org.opensingular.flow.core.variable.VarDefinition;
import org.opensingular.flow.core.variable.VarDefinitionMap;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.lib.commons.base.SingularUtil;

@SuppressWarnings("serial")
public class MTransition implements Serializable {

    private final MTask<?> origin;
    private final String name;
    private final MTask<?> destination;
    private final TransitionType type;
    private final String         abbreviation;

    private TransitionAccessStrategy<TaskInstance> accessStrategy;
    private List<MProcessRole>                     rolesToDefineUser;

    private MetaData metaData;

    private VarDefinitionMap<?>              parameters;
    private ITransitionParametersInitializer parametersInitializer;
    private ITransitionParametersValidator   parametersValidator;

    private ITaskPredicate predicate;

    protected MTransition(MTask<?> origin, String name, MTask<?> destination, TransitionType type) {
        Objects.requireNonNull(destination);
        this.origin = origin;
        this.name = name;
        this.destination = destination;
        this.type = type;
        this.abbreviation = SingularUtil.convertToJavaIdentity(name, true);
    }

    @SuppressWarnings("unchecked")
    public MTransition withAccessControl(TransitionAccessStrategy<? extends TaskInstance> accessStrategy) {
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
            for (MProcessRole processRole : rolesToDefineUser) {
                if (!processRole.isAutomaticUserAllocation()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAutomaticRoleUsersToSet() {
        if (rolesToDefineUser != null) {
            return rolesToDefineUser.stream().anyMatch(MProcessRole::isAutomaticUserAllocation);
        }
        return false;
    }

    public MTransition defineUserRoleInTransition(MProcessRole papel) {
        if (origin.isPeople() || papel.isAutomaticUserAllocation()) {
            if (this.rolesToDefineUser == null) {
                this.rolesToDefineUser = new ArrayList<>();
            }
            this.rolesToDefineUser.add(papel);
            return this;
        } else {
            throw new SingularFlowException("Only automatic user allocation is allowed in " + origin.getTaskType().toString() + " tasks");
        }
    }

    public List<MProcessRole> getRolesToDefine() {
        if (rolesToDefineUser == null) {
            return Collections.emptyList();
        }
        return rolesToDefineUser;
    }

    public <T> T getMetaDataValue(MetaDataRef<T> propRef, T defaultValue) {
        return metaData == null ? defaultValue : MoreObjects.firstNonNull(getMetaData().get(propRef), defaultValue);
    }

    public <T> T getMetaDataValue(MetaDataRef<T> propRef) {
        return metaData == null ? null : getMetaData().get(propRef);
    }

    public <T> MTransition setMetaDataValue(MetaDataRef<T> propRef, T value) {
        getMetaData().set(propRef, value);
        return this;
    }

    MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    public MTask<?> getOrigin() {
        return origin;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public MTask<?> getDestination() {
        return destination;
    }

    public MTransition thenGo(ITaskDefinition destination) {
        return thenGo(getFlowMap().getTask(destination));
    }

    public MTransition thenGo(String acao, ITaskDefinition destination) {
        return thenGo(acao, getFlowMap().getTask(destination));
    }

    public MTransition thenGo(MTask<?> destination) {
        return this.destination.addTransition(destination);
    }

    public MTransition thenGo(String acao, MTask<?> destination) {
        return this.destination.addTransition(acao, destination);
    }

    public MTransition setParametersInitializer(ITransitionParametersInitializer parametersInitializer) {
        if(this.parametersInitializer == null){
            this.parametersInitializer = parametersInitializer;
            return this;
        } else {
            throw new SingularFlowException("Parameters Initializer already set");
        }
    }

    @SuppressWarnings("unchecked")
    public <K extends ProcessInstance> MTransition setParametersInitializer(ITransitionParametersProcessInitializer<K> initializerByProcess) {
        return setParametersInitializer((ITransitionParametersInitializer) (ctx, params) -> initializerByProcess.init((K) ctx.getProcessInstance(), params));
    }

    public MTransition setParametersValidator(ITransitionParametersValidator parametersValidator) {
        if(this.parametersValidator == null){
            this.parametersValidator = parametersValidator;
            return this;
        } else {
            throw new SingularFlowException("Parameters Validator already set");
        }
    }

    @SuppressWarnings("unchecked")
    public <K extends ProcessInstance> MTransition setParametersValidator(ITransitionParametersProcessValidator<K> validatorByProcess) {
        return setParametersValidator((ITransitionParametersValidator) (ctx, params, result) -> validatorByProcess
            .validate((K) ctx.getProcessInstance(), params, result));
    }

    final VarInstanceMap<?> newTransationParameters(TransitionRef transitionRef) {
        VarInstanceMap<?> parameters = getParameters().newInstanceMap();
        if (parametersInitializer != null) {
            parametersInitializer.init(transitionRef, parameters);
        }
        return parameters;
    }

    public VarInstanceMap<?> newTransationParameters(ProcessInstance processInstance) {
        VarInstanceMap<?> parametros = getParameters().newInstanceMap();
        if (parametersInitializer != null) {
            parametersInitializer.init(new TransitionRef(processInstance, this), parametros);
        }
        return parametros;
    }

    final ValidationResult validate(TransitionRef transitionRef, VarInstanceMap<?> parameters) {
        ValidationResult validationResult = new ValidationResult();
        if (parametersValidator != null) {
            parametersValidator.validate(transitionRef, parameters, validationResult);
        }
        return validationResult;
    }

    public ValidationResult validate(ProcessInstance instancia, VarInstanceMap<?> parameters) {
        ValidationResult validationResult = new ValidationResult();
        if (parametersValidator != null) {
            parametersValidator.validate(new TransitionRef(instancia, this), parameters, validationResult);
        }
        return validationResult;
    }

    public final VarDefinitionMap<?> getParameters() {
        if (parameters == null) {
            parameters = getFlowMap().getVarService().newVarDefinitionMap();
        }
        return parameters;
    }

    public MTransition addParamFromProcessVariable(String ref, boolean required) {
        VarDefinition defVar = getFlowMap().getProcessDefinition().getVariables().getDefinition(ref);
        if (defVar == null) {
            throw new SingularFlowException(getFlowMap().createErrorMsg("Variable '" + ref + "' is not defined in process definition."));
        }
        getParameters().addVariable(defVar.copy()).setRequired(required);
        return this;
    }

    final void setPredicate(ITaskPredicate predicate) {
        this.predicate = predicate;
    }

    public ITaskPredicate getPredicate() {
        return predicate;
    }

    private FlowMap getFlowMap() {
        return destination.getFlowMap();
    }

    @Override
    public String toString() {
        return name + "(" + destination.getName() + ")";
    }

    public TransitionType getType() {
        return type;
    }

    @FunctionalInterface
    public interface ITransitionParametersInitializer extends Serializable {
        void init(TransitionRef context, VarInstanceMap<?> params);
    }

    @FunctionalInterface
    public interface ITransitionParametersProcessInitializer<K extends ProcessInstance> extends Serializable {
        void init(K processInstance, VarInstanceMap<?> params);
    }

    @FunctionalInterface
    public interface ITransitionParametersValidator extends Serializable {
        public void validate(TransitionRef context, VarInstanceMap<?> params, ValidationResult validationResult);
    }

    @FunctionalInterface
    public interface ITransitionParametersProcessValidator<K extends ProcessInstance> extends Serializable {
        void validate(K processInstance, VarInstanceMap<?> params, ValidationResult validationResult);
    }

}
