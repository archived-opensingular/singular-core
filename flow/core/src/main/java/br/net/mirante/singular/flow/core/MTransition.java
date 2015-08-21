package br.net.mirante.singular.flow.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import br.net.mirante.singular.flow.util.props.PropRef;
import br.net.mirante.singular.flow.util.props.Props;
import br.net.mirante.singular.flow.util.vars.ValidationResult;
import br.net.mirante.singular.flow.util.vars.VarDefinition;
import br.net.mirante.singular.flow.util.vars.VarDefinitionMap;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@SuppressWarnings("serial")
public class MTransition implements Serializable {

    private final MTask<?> origin;
    private final String name;
    private final MTask<?> destination;
    private final boolean userOption;

    private TransitionAccessStrategy<TaskInstance> accessStrategy;
    private List<MProcessRole> rolesToDefineUser;

    private Props properties;

    private VarDefinitionMap<?> parameters;
    private ITransitionParametersInitializer parametersInitializer;
    private ITransitionParametersValidator parametersValidator;

    private ITaskPredicate predicate;

    protected MTransition(MTask<?> origin, String name, MTask<?> destination, boolean userOption) {
        Objects.requireNonNull(destination);
        this.origin = origin;
        this.name = name;
        this.destination = destination;
        this.userOption = userOption;
    }

    @SuppressWarnings("unchecked")
    public MTransition withAccessControl(TransitionAccessStrategy<? extends TaskInstance> accessStrategy) {
        Preconditions.checkArgument(this.accessStrategy == null, "Access strategy already defined");
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
        Preconditions.checkArgument(origin.isPeople() || papel.isAutomaticUserAllocation(), "Only automatic user allocation is allowed in " + origin.getTaskType().toString() + " tasks");
        if (this.rolesToDefineUser == null) {
            this.rolesToDefineUser = new ArrayList<>();
        }
        this.rolesToDefineUser.add(papel);
        return this;
    }

    public List<MProcessRole> getRolesToDefine() {
        if (rolesToDefineUser == null) {
            return Collections.emptyList();
        }
        return rolesToDefineUser;
    }

    public <T> T getProperty(PropRef<T> propRef, T defaultValue) {
        return properties == null ? defaultValue : MoreObjects.firstNonNull(getProperties().get(propRef), defaultValue);
    }

    public <T> T getProperty(PropRef<T> propRef) {
        return properties == null ? null : getProperties().get(propRef);
    }

    public <T> MTransition setProperty(PropRef<T> propRef, T value) {
        getProperties().set(propRef, value);
        return this;
    }

    Props getProperties() {
        if (properties == null) {
            properties = new Props();
        }
        return properties;
    }
    
    public MTask<?> getOrigin() {
        return origin;
    }

    public String getName() {
        return name;
    }

    public MTask<?> getDestination() {
        return destination;
    }

    public MTransition thenGoTo(MTask<?> destination) {
        return this.destination.addTransition(destination);
    }

    public MTransition thenGoTo(String acao, MTask<?> destination) {
        return this.destination.addTransition(acao, destination);
    }

    public MTransition setParametersInitializer(ITransitionParametersInitializer parametersInitializer) {
        Preconditions.checkArgument(this.parametersInitializer == null, "Parameters Initializer already set");
        this.parametersInitializer = parametersInitializer;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <K extends ProcessInstance> MTransition setParametersInitializer(ITransitionParametersProcessInitializer<K> initializerByProcess) {
        return setParametersInitializer((ITransitionParametersInitializer) (ctx, params) -> initializerByProcess.init((K) ctx.getProcessInstance(), params));
    }

    public MTransition setParametersValidator(ITransitionParametersValidator parametersValidator) {
        Preconditions.checkArgument(this.parametersValidator == null, "Parameters Validator already set");
        this.parametersValidator = parametersValidator;
        return this;
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

    public MTransition addParameterFromProcessVariable(String ref, boolean required) {
        VarDefinition defVar = getFlowMap().getProcessDefinition().getVariables().getDefinition(ref);
        if (defVar == null) {
            throw getFlowMap().createError("Variable '" + ref + "' is not defined in process definition.");
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

    public boolean isUserOption() {
        return userOption;
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
