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
import org.opensingular.flow.core.entity.*;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.flow.core.variable.ValidationResult;
import org.opensingular.flow.core.variable.VarDefinition;
import org.opensingular.flow.core.variable.VarInstance;
import org.opensingular.flow.core.variable.VarInstanceMap;

import java.util.Date;
import java.util.Objects;
import java.util.function.BiFunction;

class FlowEngine {

    public static TaskInstance start(ProcessInstance instancia, VarInstanceMap<?> paramIn) {
        instancia.validadeStart();
        return updateState(instancia, null, null, instancia.getProcessDefinition().getFlowMap().getStartTask(), paramIn);
    }

    private static <P extends ProcessInstance> TaskInstance updateState(P instancia, TaskInstance tarefaOrigem, MTransition transicaoOrigem,
        MTask<?> taskDestino, VarInstanceMap<?> paramIn) {
        boolean primeiroLoop = true;
        while (true) {
            Date agora = new Date();
            final TaskInstance instanciaTarefa = instancia.updateState(tarefaOrigem, transicaoOrigem, taskDestino, agora);

            if (primeiroLoop) {
                inserirParametrosDaTransicao(instancia, paramIn);

                getPersistenceService().saveVariableHistoric(agora, instancia.getEntity(), tarefaOrigem, instanciaTarefa, paramIn);

                primeiroLoop = false;
            }

            getPersistenceService().flushSession();
            if (!taskDestino.isImmediateExecution()) {
                initTask(instancia, taskDestino, instanciaTarefa);
                
                if (transicaoOrigem != null && transicaoOrigem.hasAutomaticRoleUsersToSet()) {
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
                
                final ExecutionContext execucaoTask = new ExecutionContext(instancia, tarefaOrigem, paramIn, transicaoOrigem);
                if (transicaoOrigem != null) {
                    validarParametrosInput(instancia, transicaoOrigem, paramIn);
                }
                instanciaTarefa.getFlowTask().notifyTaskStart(instanciaTarefa, execucaoTask);
                return instanciaTarefa;
            }
            final ExecutionContext execucaoTask = new ExecutionContext(instancia, tarefaOrigem, paramIn, transicaoOrigem);
            instanciaTarefa.getFlowTask().notifyTaskStart(instanciaTarefa, execucaoTask);

            instancia.setExecutionContext(execucaoTask);
            execucaoTask.setTransition(null);
            try {
                if (transicaoOrigem != null) {
                    validarParametrosInput(instancia, transicaoOrigem, paramIn);
                }
                taskDestino.execute(execucaoTask);
                getPersistenceService().flushSession();
            } finally {
                instancia.setExecutionContext(null);
            }
            String nomeTransicao = execucaoTask.getTransition();
            transicaoOrigem = searchTransition(instanciaTarefa, nomeTransicao);
            taskDestino = transicaoOrigem.getDestination();
            tarefaOrigem = instanciaTarefa;
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

    public static void executeScheduledTransition(MTaskJava taskJava, ProcessInstance instancia) {
        final ExecutionContext execucaoTask = new ExecutionContext(instancia, instancia.getCurrentTask(), null);
        instancia.setExecutionContext(execucaoTask);
        try {
            taskJava.execute(execucaoTask);
        } finally {
            instancia.setExecutionContext(null);
        }

        executeTransition(instancia, execucaoTask.getTransition(), null);
    }

    static TaskInstance executeTransition(ProcessInstance instancia, String transitionName, VarInstanceMap<?> param) {
        return executeTransition(instancia.getCurrentTask(), transitionName, param);
    }

    static TaskInstance executeTransition(TaskInstance tarefaAtual, String transitionName, VarInstanceMap<?> param) {
        MTransition transicao = searchTransition(tarefaAtual, transitionName);
        tarefaAtual.endLastAllocation();
        return updateState(tarefaAtual.getProcessInstance(), tarefaAtual, transicao, transicao.getDestination(), param);
    }

    private static MTransition searchTransition(TaskInstance tarefaAtual, String nomeTransicao) {
        final MTask<?> estadoAtual = tarefaAtual.getFlowTask();

        MTransition transicao;
        if (nomeTransicao == null) {
            if (estadoAtual.getTransitions().size() == 1) {
                transicao = estadoAtual.getTransitions().get(0);
            } else if (estadoAtual.getTransitions().size() > 1 && estadoAtual.getDefaultTransition() != null) {
                transicao = estadoAtual.getDefaultTransition();
            } else {
                throw new SingularFlowException("A tarefa [" + estadoAtual.getCompleteName() + "] não definiu resultado para transicao");
            }
        } else {
            transicao = estadoAtual.getTransition(nomeTransicao);
            if (transicao == null) {
                throw new SingularFlowException("A tarefa [" + tarefaAtual.getProcessInstance().getFullId() + "." + estadoAtual.getName()
                        + "] não possui a transição '" + nomeTransicao + "' solicitada. As opções são: {"
                        + Joiner.on(',').join(estadoAtual.getTransitions()) + '}');
            }
        }
        return transicao;
    }

    private static void inserirParametrosDaTransicao(ProcessInstance instancia, VarInstanceMap<?> paramIn) {
        if (paramIn != null) {
            for (VarInstance variavel : paramIn) {
                if (instancia.getProcessDefinition().getVariables().contains(variavel.getRef())) {
                    instancia.setVariavel(variavel.getRef(), variavel.getValue());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static IPersistenceService<IEntityCategory, IEntityProcessDefinition, IEntityProcessVersion, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance> getPersistenceService() {
        return (IPersistenceService<IEntityCategory, IEntityProcessDefinition, IEntityProcessVersion, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance>) Flow
                .getConfigBean().getPersistenceService();
    }

    private static void validarParametrosInput(ProcessInstance instancia, MTransition transicao, VarInstanceMap<?> paramIn) {
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
        ValidationResult errors = transicao.validate(instancia, paramIn);
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
