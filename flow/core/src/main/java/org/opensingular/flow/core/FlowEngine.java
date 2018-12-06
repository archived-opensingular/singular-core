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

import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityFlowDefinition;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.flow.core.variable.ValidationResult;
import org.opensingular.flow.core.variable.VarDefinition;
import org.opensingular.flow.core.variable.VarInstance;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

class FlowEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowEngine.class);

    private FlowEngine() {}

    /**
     * Cria uma nova instância mediante chamada a {@link SStart#setStartInitializer(SStart.IStartInitializer)}. Senão
     * existir o inicializador, criar uma nova instância e chama {@link FlowInstance#start()}.
     */
    @Nonnull
    final static <I extends FlowInstance> I createAndStart(@Nonnull StartCall<I> startCall) {
        SStart start = startCall.getStart();
        ValidationResult result = startCall.validate();
        if (result.hasErros()) {
            throw new SingularFlowInvalidParametersException(startCall, result);
        }
        I instance = startCall.getFlowDefinition().newPreStartInstance();
        copyMarkedParametersToInstanceVariables(instance, startCall);
        if (start.getStartInitializer() != null) {
             start.getStartInitializer().startInstance(instance, (StartCall<FlowInstance>) startCall);
        } else {
            instance.start();
        }
        return instance;
    }

    public static TaskInstance start(FlowInstance instance, VarInstanceMap<?,?> paramIn) {
        validateVariables(instance);
        SStart start = instance.getFlowDefinition().getFlowMap().getStart();
        return updateState(instance, null, null, start.getTask(), paramIn);
    }

    private static void validateVariables(FlowInstance instance) {
        ValidationResult result = instance.getVariables().validate();
        if (result.hasErros()) {
            throw new SingularFlowInvalidParametersException(instance.getFlowDefinition(), result);
        }
    }

    @Nonnull
    private static <P extends FlowInstance> TaskInstance updateState(final @Nonnull P flowInstance,
            @Nullable TaskInstance originTaskInstance, @Nullable STransition transition, @Nonnull STask<?> destinyTask,
            @Nullable VarInstanceMap<?,?> paramIn) {
        Objects.requireNonNull(flowInstance);
        TaskInstance currentOrigin = originTaskInstance;
        STransition currentTransition = transition;
        STask<?> currentDestiny = Objects.requireNonNull(destinyTask);
        VarInstanceMap<?, ?> currentParam = paramIn;
        while (true) {

            checkUpdateState(flowInstance, currentOrigin, currentTransition);

            final Date         agora           = new Date();
            final TaskInstance newTaskInstance = flowInstance.updateState(currentOrigin, currentTransition, currentDestiny, agora);

            if (currentParam != null) {
                saveParam(flowInstance, currentOrigin, currentParam, agora, newTaskInstance);
            }

            getPersistenceService().flushSession();

            if (!currentDestiny.isImmediateExecution()) {
                return taskInitialization(flowInstance, currentOrigin, currentTransition, currentDestiny, currentParam,
                        newTaskInstance);
            }

            final ExecutionContext executionContext = new ExecutionContext(flowInstance, newTaskInstance, currentParam,
                    currentTransition);
            newTaskInstance.getFlowTaskOrException().notifyTaskStart(newTaskInstance, executionContext);

            flowInstance.setExecutionContext(executionContext);
            executionContext.setTransition(null);
            try {
                if (currentTransition != null) {
                    validarParametrosInput(currentOrigin, currentTransition, currentParam);
                }
                currentDestiny.execute(executionContext);
                getPersistenceService().flushSession();
            } catch(Exception e) {
                LOGGER.error(e.getMessage(), e);
                SingularFlowException e2 = new SingularFlowException(
                        "Error running task '" + currentDestiny.getName() + "'", e);
                e2.add(currentDestiny);
                throw e2;
            } finally {
                flowInstance.setExecutionContext(null);
            }

            currentTransition = resolveDefaultTransitionIfNecessary(newTaskInstance, executionContext.getTransition());
            currentDestiny = currentTransition.getDestination();
            currentOrigin = newTaskInstance;
            currentParam = null;
        }
    }

    private static <P extends FlowInstance> void saveParam(@Nonnull P flowInstance, TaskInstance currentOrigin, VarInstanceMap<?, ?> currentParam, Date agora, TaskInstance newTaskInstance) {
        copyMarkedParametersToInstanceVariables(flowInstance, currentParam);
        if (currentOrigin != null) {
            //TODO (Daniel) o If acima existe para não dar erro a iniciar fluxo com variáveis setadas no
            // start, mas deveria guardar no histórico da variavel originais do start (o que o if a cima
            // impede). O problema é uqe originTaskInstance é obrigatório
            getPersistenceService().saveVariableHistoric(agora, flowInstance.getEntity(), currentOrigin, newTaskInstance, currentParam);
        }
    }

    private static <P extends FlowInstance> void checkUpdateState(@Nonnull P flowInstance, TaskInstance currentOrigin, STransition currentTransition) {
        if (isCurrentTransitionNotNullAndCurrentOriginNull(currentOrigin, currentTransition)) {
            throw new SingularFlowException(
                    "Não pode ser solicitada execução de uma transição específica (transition=" +
                            currentTransition.getName() +
                            ") sem uma instancia de tarefa de origem (tarefaOrigem null)", flowInstance);
        } else if (isCurrentOriginNotNullAndIsntActive(currentOrigin)) {
            throw new SingularFlowException(
                    "Não pode ser executada uma transição a partir da task '" + currentOrigin.getName() +
                            "', pois a mesma já está concluida.", currentOrigin);
        }
    }

    private static boolean isCurrentOriginNotNullAndIsntActive(TaskInstance currentOrigin) {
        return currentOrigin != null && !currentOrigin.isActive();
    }

    private static boolean isCurrentTransitionNotNullAndCurrentOriginNull(TaskInstance currentOrigin, STransition currentTransition) {
        return currentTransition != null && currentOrigin == null;
    }

    private static <P extends FlowInstance> TaskInstance taskInitialization(P flowInstance, TaskInstance originTaskInstance,
                                                                            STransition transition, STask<?> destinyTask, VarInstanceMap<?, ?> paramIn,
                                                                            @Nonnull TaskInstance newTaskInstance) {
        initTask(flowInstance, destinyTask, newTaskInstance);

        if (transition != null && transition.hasAutomaticRoleUsersToSet()) {
            automaticallySetBusinessRole(flowInstance, newTaskInstance, transition);
        }

        if (transition != null) {
            validarParametrosInput(originTaskInstance, transition, paramIn);
        }
        ExecutionContext executionContext = new ExecutionContext(flowInstance, newTaskInstance, paramIn, transition);
        newTaskInstance.getFlowTaskOrException().notifyTaskStart(newTaskInstance, executionContext);
        return newTaskInstance;
    }

    private static <P extends FlowInstance> void automaticallySetBusinessRole(@Nonnull P instance,
            @Nonnull TaskInstance taskInstance, @Nonnull STransition originTransition) {
        for (SBusinessRole role : originTransition.getRolesToDefine()) {
            if (role.isAutomaticBusinessRoleAllocation()) {
                Optional<SUser> user = role.getBusinessRoleStrategy().getUserForRole(instance, taskInstance);
                if (user.isPresent()) {
                    instance.addOrReplaceUserRole(role.getAbbreviation(), user.get());
                } else {
                    throw new SingularFlowException(
                            "Não foi possível determinar a pessoa com o papel " + role.getName() + " para " +
                                    instance.getFullId() + " na transição " + originTransition.getName(), taskInstance);
                }
            }
        }
    }

    public static <P extends FlowInstance> void initTask(P instance, STask<?> taskDestiny, TaskInstance taskInstance) {
        if (taskDestiny.isWait()) {
            initTaskWait(instance, (STaskWait) taskDestiny, taskInstance);
        } else if (taskDestiny.isPeople()) {
            initTaskHuman(instance, (STaskHuman) taskDestiny, taskInstance);
        }
    }

    private static <P extends FlowInstance> void initTaskHuman(P instance, STaskHuman taskDestiny,
                                                               TaskInstance taskInstance) {
        TaskAccessStrategy<FlowInstance> strategy = taskDestiny.getAccessStrategy();
        if (strategy != null) {
            SUser person = strategy.getAutomaticAllocatedUser(instance, taskInstance);
            if (person != null && Flow.canBeAllocated(person)) {
                taskInstance.relocateTask(null, person,
                        strategy.isNotifyAutomaticAllocation(instance, taskInstance), null);
            }
        }
        BiFunction<FlowInstance, TaskInstance, Date> strategyDate = taskDestiny.getTargetDateExecutionStrategy();
        if (strategyDate != null) {
            Date targetDate = strategyDate.apply(instance, taskInstance);
            if (targetDate != null) {
                taskInstance.setTargetEndDate(targetDate);
            }
        }
    }

    private static <P extends FlowInstance> void initTaskWait(P instance, STaskWait taskDestiny,
            TaskInstance taskInstance) {
        if (taskDestiny.hasExecutionDateStrategy()) {
            Date targetDate = taskDestiny.getExecutionDate(instance, taskInstance);
            taskInstance.setTargetEndDate(targetDate);
            if (targetDate.before(new Date())) {
                instance.prepareTransition().go();
            }
        } else if (taskDestiny.getTargetDateExecutionStrategy() != null) {
            Date targetDate = taskDestiny.getTargetDateExecutionStrategy().apply(instance, taskInstance);
            if (targetDate != null) {
                taskInstance.setTargetEndDate(targetDate);
            }
        }
    }

    public static void executeScheduledTransition(@Nonnull STaskJava taskJava, @Nonnull FlowInstance instance) {
        Objects.requireNonNull(instance);
        Objects.requireNonNull(taskJava);
        ExecutionContext executionContext = new ExecutionContext(instance, instance.getCurrentTaskOrException(), null);
        instance.setExecutionContext(executionContext);
        try {
            taskJava.execute(executionContext);
        } finally {
            instance.setExecutionContext(null);
        }

        executeTransition(instance.getCurrentTaskOrException(), executionContext.getTransition(), null);
    }

    @Nonnull
    static TaskInstance executeTransition(@Nonnull TaskInstance currentTask, @Nullable STransition transition,
            @Nullable VarInstanceMap<?, ?> param) {
        STransition trans = resolveDefaultTransitionIfNecessary(currentTask, transition);
        currentTask.endLastAllocation();
        return updateState(currentTask.getFlowInstance(), currentTask, trans, trans.getDestination(), param);
    }


    @Nonnull
    private static STransition resolveDefaultTransitionIfNecessary(@Nonnull TaskInstance currentTask,
            @Nullable STransition transition) {
        if (transition != null) {
            return transition;
        }
        try {
            return currentTask.getFlowTaskOrException().resolveDefaultTransitionOrException();
        } catch (SingularFlowTransactionNotFoundException e) {
            e.add("complement",
                    "A execução da task deve explicitamente definir qual transação deve ser seguida ou o fluxo dever " +
                            "ser configurado para ter uma transição como default (a ser usada quando não for " +
                            "especificada uma transação)");
            throw e;
        }
    }

    /**
     * Copia para a instancia os paramentros que estiverem marcados com bind automáticos (copia automática) para as
     * variaveis da instância. Além de marcados, devem ter o mesmo nome.
     */
    private static void copyMarkedParametersToInstanceVariables(@Nonnull FlowInstance instance,
            @Nonnull VarInstanceMap<?, ?> paramIn) {
        for (VarInstance variavel : paramIn) {
            if (variavel.getValue() != null && SParametersEnabled.isAutoBindedToFlowVariable(variavel)) {
                String ref = variavel.getRef();
                if (instance.getFlowDefinition().getVariables().contains(ref)) {
                    instance.setVariable(ref, variavel.getValue());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static IPersistenceService<IEntityCategory, IEntityFlowDefinition, IEntityFlowVersion, IEntityFlowInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance> getPersistenceService() {
        return (IPersistenceService<IEntityCategory, IEntityFlowDefinition, IEntityFlowVersion, IEntityFlowInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance>) Flow
                .getConfigBean().getPersistenceService();
    }

    private static void validarParametrosInput(TaskInstance taskInstance, @Nonnull STransition transition, VarInstanceMap<?,?> paramIn) {
        Objects.requireNonNull(taskInstance);
        if (transition.getParameters().isEmpty()) {
            return;
        }
        ValidationResult errors = new ValidationResult();
        for (VarDefinition p : transition.getParameters()) {
            if (p.isRequired() && !parametroPresentes(paramIn, p)) {
                errors.addErro(p, "parametro obrigatório não informado");
            }
        }
        if (! errors.hasErros()) {
            errors = transition.validate(taskInstance, paramIn);
        }
        if (errors.hasErros()) {
            throw new SingularFlowInvalidParametersException(taskInstance, transition, errors);
        }
    }

    private static boolean parametroPresentes(VarInstanceMap<?,?> parametros, VarDefinition parametroEsperado) {
        return parametros != null && parametros.getValue(parametroEsperado.getRef()) != null;
    }
}
