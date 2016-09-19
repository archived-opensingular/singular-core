/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core;

import java.util.Date;
import java.util.Objects;
import java.util.function.BiFunction;

import com.google.common.base.Joiner;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityRoleDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityRoleInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.core.variable.ValidationResult;
import br.net.mirante.singular.flow.core.variable.VarDefinition;
import br.net.mirante.singular.flow.core.variable.VarInstance;
import br.net.mirante.singular.flow.core.variable.VarInstanceMap;

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
                
                if (taskDestino.isPeople() && transicaoOrigem != null && transicaoOrigem.hasAutomaticRoleUsersToSet()) {
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
            try {
                if (transicaoOrigem != null) {
                    validarParametrosInput(instancia, transicaoOrigem, paramIn);
                }
                taskDestino.execute(execucaoTask);
                getPersistenceService().flushSession();
            } finally {
                instancia.setExecutionContext(null);
            }
            transicaoOrigem = searchTransition(instanciaTarefa, null);
            taskDestino = transicaoOrigem.getDestination();
            tarefaOrigem = instanciaTarefa;
        }
    }

    public static <P extends ProcessInstance> void initTask(P instancia, MTask<?> taskDestino, final TaskInstance instanciaTarefa) {
        if (taskDestino.isWait()) {
            final MTaskWait mTaskWait = (MTaskWait) taskDestino;
            if (mTaskWait.hasExecutionDateStrategy()) {
                final Date dataExecucao = mTaskWait.getExecutionDate(instancia, instanciaTarefa);
                instanciaTarefa.setTargetEndDate(dataExecucao);
                if (dataExecucao.before(new Date())) {
                    instancia.executeTransition();
                }
            } else if (mTaskWait.getTargetDateExecutionStrategy() != null) {
                Date alvo = mTaskWait.getTargetDateExecutionStrategy().apply(instancia, instanciaTarefa);
                if (alvo != null) {
                    instanciaTarefa.setTargetEndDate(alvo);
                }
            }
        } else if (taskDestino.isPeople()) {
            final MTaskPeople taskPessoa = (MTaskPeople) taskDestino;
            final TaskAccessStrategy<ProcessInstance> estrategia = taskPessoa.getAccessStrategy();
            if (estrategia != null) {
                MUser pessoa = estrategia.getAutomaticAllocatedUser(instancia, instanciaTarefa);
                if (pessoa != null && Flow.canBeAllocated(pessoa)) {
                    instanciaTarefa.relocateTask(null, pessoa, estrategia.isNotifyAutomaticAllocation(instancia, instanciaTarefa), null);
                }
            }
            final BiFunction<ProcessInstance, TaskInstance, Date> estrategiaData = taskPessoa.getTargetDateExecutionStrategy();
            if (estrategiaData != null) {
                Date alvo = estrategiaData.apply(instancia, instanciaTarefa);
                if (alvo != null) {
                    instanciaTarefa.setTargetEndDate(alvo);
                }
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
                    instancia.setVariavel(variavel.getRef(), variavel.getValor());
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
