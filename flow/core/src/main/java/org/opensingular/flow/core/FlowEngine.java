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
import org.opensingular.flow.core.entity.IEntityProcessDefinition;
import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.entity.IEntityProcessVersion;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.Objects;
import java.util.function.BiFunction;

class FlowEngine {

    private FlowEngine() {}

    public static TaskInstance start(ProcessInstance instancia, VarInstanceMap<?> paramIn) {
        instancia.validadeStart();
        MStart start = instancia.getProcessDefinition().getFlowMap().getStart();
        return updateState(instancia, null, null, start.getTask(), paramIn);
    }

    @Nonnull
    private static <P extends ProcessInstance> TaskInstance updateState(final @Nonnull P processInstance,
            @Nullable TaskInstance originTaskInstance, @Nullable MTransition transition, @Nonnull MTask<?> destinyTask,
            @Nullable VarInstanceMap<?> paramIn) {
        Objects.requireNonNull(processInstance);
        Objects.requireNonNull(destinyTask);
        while (true) {
            if (transition != null && originTaskInstance == null) {
                throw new SingularFlowException(
                        "Não pode ser solicitada execução de uma transição específica (transition=" +
                                transition.getName() + ") sem uma instancia de tarefa de origem (tarefaOrigem null)", processInstance);
            }
            Date agora = new Date();
            final TaskInstance newTaskInstance = processInstance.updateState(originTaskInstance, transition, destinyTask, agora);

            if (paramIn != null) {
                inserirParametrosDaTransicao(processInstance, paramIn);

                if (originTaskInstance != null) {
                    //TODO (Daniel) o If acima existe para não dar erro a iniciar processo com variáveis setadas no
                    // start, mas deveria guardar no histórico da variavel originais do start (o que o if a cima
                    // impede). O problema é uqe originTaskInstance é obrigatório
                    getPersistenceService().saveVariableHistoric(agora, processInstance.getEntity(), originTaskInstance,
                            newTaskInstance, paramIn);
                }
            }

            getPersistenceService().flushSession();
            if (!destinyTask.isImmediateExecution()) {
                initTask(processInstance, destinyTask, newTaskInstance);
                
                if (transition != null && transition.hasAutomaticRoleUsersToSet()) {
                    automaticallySetUsersRole(processInstance, newTaskInstance, transition);
                }
                
                if (transition != null) {
                    validarParametrosInput(originTaskInstance, transition, paramIn);
                }
                ExecutionContext execucaoTask = new ExecutionContext(processInstance, originTaskInstance, paramIn, transition);
                newTaskInstance.getFlowTaskOrException().notifyTaskStart(newTaskInstance, execucaoTask);
                return newTaskInstance;
            }
            final ExecutionContext execucaoTask = new ExecutionContext(processInstance, originTaskInstance, paramIn, transition);
            newTaskInstance.getFlowTaskOrException().notifyTaskStart(newTaskInstance, execucaoTask);

            processInstance.setExecutionContext(execucaoTask);
            execucaoTask.setTransition(null);
            try {
                if (transition != null) {
                    validarParametrosInput(originTaskInstance, transition, paramIn);
                }
                destinyTask.execute(execucaoTask);
                getPersistenceService().flushSession();
            } finally {
                processInstance.setExecutionContext(null);
            }

            transition = resolveDefaultTransitionIfNecessary(newTaskInstance, execucaoTask.getTransition());
            destinyTask = transition.getDestination();
            originTaskInstance = newTaskInstance;
            paramIn = null;
        }
    }

    private static <P extends ProcessInstance> void automaticallySetUsersRole(P instancia, TaskInstance instanciaTarefa,
            MTransition transicaoOrigem) {
        for (MProcessRole papel : transicaoOrigem.getRolesToDefine()) {
            if (papel.isAutomaticUserAllocation()) {
                MUser pessoa = papel.getUserRoleSettingStrategy().getAutomaticAllocatedUser(instancia,
                    instanciaTarefa);
                Objects.requireNonNull(pessoa, "Não foi possível determinar a pessoa com o papel " + papel.getName()
                        + " para " + instancia.getFullId() + " na transição " + transicaoOrigem.getName());

                instancia.addOrReplaceUserRole(papel.getAbbreviation(), pessoa);
            }
        }
    }

    public static <P extends ProcessInstance> void initTask(P instance, MTask<?> taskDestiny, TaskInstance taskInstance) {
        if (taskDestiny.isWait()) {
            initTaskWait(instance, (MTaskWait) taskDestiny, taskInstance);
        } else if (taskDestiny.isPeople()) {
            initTaskPeople(instance, (MTaskPeople) taskDestiny, taskInstance);
        }
    }

    private static <P extends ProcessInstance> void initTaskPeople(P instance, MTaskPeople taskDestiny,
            TaskInstance taskInstance) {
        TaskAccessStrategy<ProcessInstance> strategy = taskDestiny.getAccessStrategy();
        if (strategy != null) {
            MUser person = strategy.getAutomaticAllocatedUser(instance, taskInstance);
            if (person != null && Flow.canBeAllocated(person)) {
                taskInstance.relocateTask(null, person,
                        strategy.isNotifyAutomaticAllocation(instance, taskInstance), null);
            }
        }
        BiFunction<ProcessInstance, TaskInstance, Date> strategyDate = taskDestiny.getTargetDateExecutionStrategy();
        if (strategyDate != null) {
            Date targetDate = strategyDate.apply(instance, taskInstance);
            if (targetDate != null) {
                taskInstance.setTargetEndDate(targetDate);
            }
        }
    }

    private static <P extends ProcessInstance> void initTaskWait(P instance, MTaskWait taskDestiny,
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

    public static void executeScheduledTransition(@Nonnull MTaskJava taskJava, @Nonnull ProcessInstance instance) {
        Objects.requireNonNull(instance);
        Objects.requireNonNull(taskJava);
        ExecutionContext execucaoTask = new ExecutionContext(instance, instance.getCurrentTaskOrException(), null);
        instance.setExecutionContext(execucaoTask);
        try {
            taskJava.execute(execucaoTask);
        } finally {
            instance.setExecutionContext(null);
        }

        executeTransition(instance.getCurrentTaskOrException(), execucaoTask.getTransition(), null);
    }

    @Nonnull
    static TaskInstance executeTransition(@Nonnull TaskInstance tarefaAtual, @Nullable MTransition transition, @Nullable VarInstanceMap<?> param) {
        transition = resolveDefaultTransitionIfNecessary(tarefaAtual, transition);
        tarefaAtual.endLastAllocation();
        return updateState(tarefaAtual.getProcessInstance(), tarefaAtual, transition, transition.getDestination(), param);
    }


    @Nonnull
    private static MTransition resolveDefaultTransitionIfNecessary(@Nonnull TaskInstance tarefaAtual,
            @Nullable MTransition transition) {
        if (transition != null) {
            return transition;
        }
        return tarefaAtual.getFlowTaskOrException().resolveDefaultTransitionOrException();
    }

    private static void inserirParametrosDaTransicao(@Nonnull ProcessInstance instancia, @Nonnull VarInstanceMap<?> paramIn) {
        for (VarInstance variavel : paramIn) {
            String ref = variavel.getRef();
            if (instancia.getProcessDefinition().getVariables().contains(ref)) {
                instancia.setVariable(ref, variavel.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static IPersistenceService<IEntityCategory, IEntityProcessDefinition, IEntityProcessVersion, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance> getPersistenceService() {
        return (IPersistenceService<IEntityCategory, IEntityProcessDefinition, IEntityProcessVersion, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance>) Flow
                .getConfigBean().getPersistenceService();
    }

    private static void validarParametrosInput(@Nonnull TaskInstance taskInstance, @Nonnull MTransition transicao, VarInstanceMap<?> paramIn) {
        Objects.requireNonNull(taskInstance);
        if (transicao.getParameters().isEmpty()) {
            return;
        }
        for (VarDefinition p : transicao.getParameters()) {
            if (p.isRequired()) {
                if (!parametroPresentes(paramIn, p)) {
                    throw new SingularFlowException(
                            "O parametro obrigatório '" + p.getRef() + "' não foi informado na chamada da transição " +
                                    transicao.getName(), taskInstance);
                }
            }
        }
        ValidationResult errors = transicao.validate(taskInstance, paramIn);
        if (errors.hasErros()) {
            throw new SingularFlowException(
                    "Erro ao validar os parametros da transição " + transicao.getName() + " [" + errors + "]",
                    taskInstance);
        }
    }

    private static boolean parametroPresentes(VarInstanceMap<?> parametros, VarDefinition parametroEsperado) {
        if (parametros == null) {
            return false;
        }
        return parametros.contains(parametroEsperado.getRef());
    }
}
