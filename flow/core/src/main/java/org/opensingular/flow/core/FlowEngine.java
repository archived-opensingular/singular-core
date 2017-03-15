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

import com.google.common.base.Joiner;
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
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

class FlowEngine {

    private FlowEngine() {}

    public static TaskInstance start(ProcessInstance instancia, VarInstanceMap<?> paramIn) {
        instancia.validadeStart();
        return updateState(instancia, null, null, instancia.getProcessDefinition().getFlowMap().getStartTask(), paramIn);
    }

    @Nonnull
    private static <P extends ProcessInstance> TaskInstance updateState(final @Nonnull P processInstance,
            @Nullable TaskInstance originTaskInstance, @Nullable MTransition transition, @Nonnull MTask<?> destinyTask,
            @Nullable VarInstanceMap<?> paramIn) {
        Objects.requireNonNull(processInstance);
        Objects.requireNonNull(destinyTask);
        boolean primeiroLoop = true;
        while (true) {
            if (transition != null && originTaskInstance == null) {
                throw new SingularFlowException(
                        "Não pode ser solicitada execução de uma transição específica (transition=" +
                                transition.getName() + ") sem uma instancia de tarefa de origem (tarefaOrigem null)");
            }
            Date agora = new Date();
            final TaskInstance newTaskInstance = processInstance.updateState(originTaskInstance, transition, destinyTask, agora);

            if (primeiroLoop) {
                inserirParametrosDaTransicao(processInstance, paramIn);

                getPersistenceService().saveVariableHistoric(agora, processInstance.getEntity(), originTaskInstance, newTaskInstance, paramIn);

                primeiroLoop = false;
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
            String nomeTransicao = execucaoTask.getTransition();
            transition = searchTransition(newTaskInstance, nomeTransicao);
            destinyTask = transition.getDestination();
            originTaskInstance = newTaskInstance;
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
                instance.executeTransition();
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

        executeTransition(instance, execucaoTask.getTransition(), null);
    }

    @Nonnull
    static TaskInstance executeTransition(@Nonnull ProcessInstance instancia, @Nullable String transitionName, @Nullable VarInstanceMap<?> param) {
        return executeTransition(instancia.getCurrentTaskOrException(), transitionName, param);
    }

    @Nonnull
    static TaskInstance executeTransition(@Nonnull TaskInstance tarefaAtual, @Nullable String transitionName, @Nullable VarInstanceMap<?> param) {
        MTransition transicao = searchTransition(tarefaAtual, transitionName);
        tarefaAtual.endLastAllocation();
        return updateState(tarefaAtual.getProcessInstance(), tarefaAtual, transicao, transicao.getDestination(), param);
    }

    @Nonnull
    private static MTransition searchTransition(@Nonnull TaskInstance tarefaAtual, @Nullable String nomeTransicao) {

        final MTask<?> estadoAtual = tarefaAtual.getFlowTaskOrException();
        final MTransition transicao;
        final List<MTransition> transitions = estadoAtual.getTransitions();

        if (nomeTransicao == null) {
            if (transitions.size() == 1) {
                transicao = transitions.get(0);
            } else {

                MTransition defaultTransition = estadoAtual.getDefaultTransition();

                if (transitions.size() > 1 && defaultTransition != null) {
                    transicao = defaultTransition;
                } else {
                    throw new SingularFlowException("A tarefa [" + estadoAtual.getCompleteName() + "] não definiu resultado para transicao");
                }
            }
        } else {
            transicao = estadoAtual.getTransition(nomeTransicao);
            if (transicao == null) {
                throw new SingularFlowException("A tarefa [" + tarefaAtual.getProcessInstance().getFullId() + "." + estadoAtual.getName()
                        + "] não possui a transição '" + nomeTransicao + "' solicitada. As opções são: {"
                        + Joiner.on(',').join(transitions) + '}');
            }
        }
        return transicao;
    }

    private static void inserirParametrosDaTransicao(ProcessInstance instancia, VarInstanceMap<?> paramIn) {
        if (paramIn != null) {
            for (VarInstance variavel : paramIn) {
                String ref = variavel.getRef();
                if (instancia.getProcessDefinition().getVariables().contains(ref)) {
                    instancia.setVariable(ref, variavel.getValue());
                }
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
                    throw new SingularFlowException("O parametro obrigatório '" + p.getRef()
                            + "' não foi informado na chamada da transição "
                        + transicao.getName());
                }
            }
        }
        ValidationResult errors = transicao.validate(taskInstance, paramIn);
        if (errors.hasErros()) {
            throw new SingularFlowException("Erro ao validar os parametros da transição " + transicao.getName() + " [" + errors + "]");
        }
    }

    private static boolean parametroPresentes(VarInstanceMap<?> parametros, VarDefinition parametroEsperado) {
        if (parametros == null) {
            return false;
        }
        return parametros.contains(parametroEsperado.getRef());
    }
}
