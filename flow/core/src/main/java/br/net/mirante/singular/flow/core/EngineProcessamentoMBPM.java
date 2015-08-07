package br.net.mirante.singular.flow.core;

import java.util.Date;
import java.util.function.BiFunction;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import br.net.mirante.singular.flow.util.vars.ValidationResult;
import br.net.mirante.singular.flow.util.vars.VarDefinition;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.core.entity.persistence.IPersistenceService;

class EngineProcessamentoMBPM {

    public static TaskInstance iniciar(ProcessInstance instancia, VarInstanceMap<?> paramIn) {
        instancia.validarPreInicio();
        return updateEstado(instancia, null, null, instancia.getDefinicao().getFluxo().getTaskInicial(), paramIn);
    }

    private static TaskInstance updateEstado(ProcessInstance instancia, TaskInstance tarefaOrigem, MTransition transicaoOrigem,
            MTask<?> taskDestino, VarInstanceMap<?> paramIn) {
        boolean primeiroLoop = true;
        while (true) {
            Date agora = new Date();
            final TaskInstance instanciaTarefa = instancia.updateEstado(tarefaOrigem, transicaoOrigem, taskDestino, agora);

            if (primeiroLoop) {
                inserirParametrosDaTransicao(instancia, paramIn);
                salvarHistoricoVariavel(agora, instancia, tarefaOrigem, instanciaTarefa, paramIn);
                primeiroLoop = false;
            }

            getPersistenceService().flushSession();
            if (!taskDestino.isImmediateExecution()) {
                if (taskDestino.isWait()) {
                    final MTaskWait mTaskWait = (MTaskWait) taskDestino;
                    if (mTaskWait.hasExecutionDateStrategy()) {
                        final Date dataExecucao = mTaskWait.getExecutionDate(instancia, instanciaTarefa);
                        instanciaTarefa.setDataAlvoFim(dataExecucao);
                        if (dataExecucao.before(new Date())) {
                            instancia.executarTransicao();
                        }
                    } else if (mTaskWait.getTargetDateExecutionStrategy() != null) {
                        Date alvo = mTaskWait.getTargetDateExecutionStrategy().apply(instancia, instanciaTarefa);
                        if (alvo != null) {
                            instanciaTarefa.setDataAlvoFim(alvo);
                        }
                    }
                } else if (taskDestino.isPeople()) {
                    final MTaskPeople taskPessoa = (MTaskPeople) taskDestino;
                    final TaskAccessStrategy<ProcessInstance> estrategia = taskPessoa.getAccessStrategy();
                    if (estrategia != null) {
                        MUser pessoa = estrategia.getAutomaticAllocatedUser(instancia, instanciaTarefa);
                        if (pessoa != null && MBPM.isPessoaAtivaParaTerTarefa(pessoa)) {
                            instanciaTarefa.relocateTask(null, pessoa, estrategia.isNotifyAutomaticAllocation(instancia, instanciaTarefa), null);
                        }
                    }
                    final BiFunction<ProcessInstance, TaskInstance, Date> estrategiaData = taskPessoa.getTargetDateExecutionStrategy();
                    if (estrategiaData != null) {
                        Date alvo = estrategiaData.apply(instancia, instanciaTarefa);
                        if (alvo != null) {
                            instanciaTarefa.setDataAlvoFim(alvo);
                        }
                    }
                    if (transicaoOrigem != null && transicaoOrigem.hasAutomaticRoleToSet()) {
                        for (MProcessRole papel : transicaoOrigem.getRolesToDefine()) {
                            if (papel.isAutomaticUserAllocation()) {
                                MUser pessoa = papel.getUserRoleSettingStrategy().getAutomaticAllocatedUser(instancia,
                                        instanciaTarefa);
                                Preconditions.checkNotNull(pessoa, "Não foi possível determinar a pessoa com o papel " + papel.getName()
                                        + " para " + instancia.getFullId() + " na transição " + transicaoOrigem.getName());

                                instancia.addOrReplacePapelPessoa(papel.getAbbreviation(), pessoa);
                            }
                        }
                    }
                }
                final ExecucaoMTask execucaoTask = new ExecucaoMTask(instancia, paramIn);
                if (transicaoOrigem != null) {
                    validarParametrosInput(instancia, transicaoOrigem, paramIn);
                }
                instanciaTarefa.getTipo().notifyTaskStart(instanciaTarefa, execucaoTask);
                return instanciaTarefa;
            }
            final ExecucaoMTask execucaoTask = new ExecucaoMTask(instancia, paramIn);
            instanciaTarefa.getTipo().notifyTaskStart(instanciaTarefa, execucaoTask);

            instancia.setContextoExecucao(execucaoTask);
            try {
                if (transicaoOrigem != null) {
                    validarParametrosInput(instancia, transicaoOrigem, paramIn);
                }
                taskDestino.execute(execucaoTask);
                getPersistenceService().flushSession();
            } finally {
                instancia.setContextoExecucao(null);
            }
            final String nomeTransicao = execucaoTask.getTransicaoResultado();
            transicaoOrigem = tratarTransicao(instanciaTarefa, nomeTransicao);
            taskDestino = transicaoOrigem.getDestination();
            tarefaOrigem = instanciaTarefa;
        }
    }

    public static void executarTransicaoAgendada(MTaskJava taskJava, ProcessInstance instancia) {
        final ExecucaoMTask execucaoTask = new ExecucaoMTask(instancia, null);
        instancia.setContextoExecucao(execucaoTask);
        try {
            taskJava.execute(execucaoTask);
        } finally {
            instancia.setContextoExecucao(null);
        }

        executarTransicao(instancia, execucaoTask.getTransicaoResultado(), null);
    }

    static TaskInstance executarTransicao(ProcessInstance instancia, String nomeTransicao, VarInstanceMap<?> param) {
        return executeTransition(instancia.getTarefaAtual(), nomeTransicao, param);
    }

    static TaskInstance executeTransition(TaskInstance tarefaAtual, String nomeTransicao, VarInstanceMap<?> param) {
        MTransition transicao = tratarTransicao(tarefaAtual, nomeTransicao);
        return updateEstado(tarefaAtual.getProcessInstance(), tarefaAtual, transicao, transicao.getDestination(), param);
    }

    private static MTransition tratarTransicao(TaskInstance tarefaAtual, String nomeTransicao) {
        final MTask<?> estadoAtual = tarefaAtual.getTipo();

        MTransition transicao;
        if (nomeTransicao == null) {
            if (estadoAtual.getTransicoes().size() == 1) {
                transicao = estadoAtual.getTransicoes().get(0);
            } else if (estadoAtual.getTransicoes().size() > 1 && estadoAtual.getDefaultTransition() != null) {
                transicao = estadoAtual.getDefaultTransition();
            } else {
                throw new RuntimeException("A tarefa [" + estadoAtual.getCompleteName() + "] não definiu resultado para transicao");
            }
        } else {
            transicao = estadoAtual.getTransition(nomeTransicao);
            if (transicao == null) {
                throw new RuntimeException("A tarefa [" + tarefaAtual.getProcessInstance().getFullId() + "." + estadoAtual.getName() + "] não possui a transição '" + nomeTransicao
                        + "' solicitada. As opções são: {" + listarTransicoes(estadoAtual) + '}');
            }
        }
        return transicao;
    }

    private static String listarTransicoes(MTask<?> estadoAtual) {
        return Joiner.on(',').join(estadoAtual.getTransicoes());
    }

    private static void inserirParametrosDaTransicao(ProcessInstance instancia, VarInstanceMap<?> paramIn) {
        if (paramIn != null) {
            for (VarInstance variavel : paramIn) {
                if (instancia.getDefinicao().getVariaveis().contains(variavel.getRef())) {
                    instancia.setVariavel(variavel.getRef(), variavel.getValor());
                }
            }
        }
    }

    private static void salvarHistoricoVariavel(Date agora, ProcessInstance instancia, TaskInstance tarefaOrigem,
            TaskInstance tarefaDestino, VarInstanceMap<?> paramIn) {
        if (paramIn != null) {
            getPersistenceService().salvarHistoricoVariavel(agora, instancia.getDemanda(),
                    tarefaOrigem != null ? tarefaOrigem.getEntityTaskInstance() : null,
                    tarefaDestino != null ? tarefaDestino.getEntityTaskInstance() : null, paramIn);
        }
    }

    public static IPersistenceService<?, ?, ?, ?, ?, ?> getPersistenceService() {
        return MBPM.getMbpmBean().getPersistenceService();
    }

    private static void validarParametrosInput(ProcessInstance instancia, MTransition transicao, VarInstanceMap<?> paramIn) {
        if (transicao.getParameters().isEmpty()) {
            return;
        }
        for (VarDefinition p : transicao.getParameters()) {
            if (p.isRequired()) {
                if (!parametroPresentes(paramIn, p)) {
                    throw new RuntimeException("O parametro obrigatório '" + p.getRef() + "' não foi informado na chamada da transição "
                            + transicao.getName());
                }
            }
        }
        ValidationResult errors = transicao.validate(instancia, paramIn);
        if (errors.hasErros()) {
            throw new RuntimeException("Erro ao validar os parametros da transição " + transicao.getName() + " [" + errors + "]");
        }
    }

    private static boolean parametroPresentes(VarInstanceMap<?> parametros, VarDefinition parametroEsperado) {
        if (parametros == null) {
            return false;
        }
        return parametros.contains(parametroEsperado.getRef());
    }
}
