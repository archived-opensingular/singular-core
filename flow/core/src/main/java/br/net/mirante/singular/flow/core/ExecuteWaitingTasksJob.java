package br.net.mirante.singular.flow.core;

import java.util.Date;
import java.util.List;

import br.net.mirante.singular.flow.schedule.IScheduleData;
import br.net.mirante.singular.flow.schedule.IScheduledJob;

public class ExecuteWaitingTasksJob implements IScheduledJob {

    private final IScheduleData scheduleData;

    public ExecuteWaitingTasksJob(IScheduleData scheduleData) {
        super();
        this.scheduleData = scheduleData;
    }

    @Override
    public String getId() {
        return "ExecuteWaitingTasks";
    }

    @Override
    public IScheduleData getScheduleData() {
        return scheduleData;
    }

    @Override
    public Object run() {
        final AbstractMbpmBean mbpmBean = MBPM.getMbpmBean();
        final StringBuilder log = new StringBuilder();
        final Date hoje = new Date();
        for (ProcessDefinition<?> definicaoProcessoMBPM : mbpmBean.getDefinitions()) {
            for (final MTaskWait task : definicaoProcessoMBPM.getFlowMap().getWaitTasks()) {
                if (task.hasExecutionDateStrategy()) {
                    for (ProcessInstance instancia : definicaoProcessoMBPM.getInstanciasNoEstado(task)) {
                        TaskInstance instanciaTarefa = instancia.getTarefaAtual();
                        Date dataExecucao = task.getExecutionDate(instancia, instanciaTarefa);
                        if (!dataExecucao.equals(instancia.getTarefaAtual().getDataAlvoFim())) {
                            instancia.getTarefaAtual().setDataAlvoFim(dataExecucao);
                        }
                        if (hoje.after(dataExecucao)) {
                            log.append("Executando transição da instância: ").append(instancia.getFullId()).append("\n");
                            instancia.executarTransicao();
                            mbpmBean.getPersistenceService().commitTransaction();
                        }

                    }
                }
            }
        }

        for (ProcessDefinition<?> definicaoProcessoMBPM : mbpmBean.getDefinitions()) {
            definicaoProcessoMBPM.getFlowMap().getPeopleTasks().stream()
                    .filter(task -> task.getTargetDateExecutionStrategy() != null)
                    .forEach(task -> {
                        // Preenche Data Alvo para os casos que estiverem null
                        for (ProcessInstance instancia : definicaoProcessoMBPM.getInstanciasNoEstado(task)) {
                            TaskInstance instanciaTarefa = instancia.getTarefaAtual();
                            if (instanciaTarefa.getDataAlvoFim() == null) {
                                Date alvo = task.getTargetDateExecutionStrategy().apply(instancia, instanciaTarefa);
                                if (alvo != null) {
                                    log.append("Alterando data alvo: ").append(instancia.getFullId()).append(" para ").append(alvo)
                                            .append("\n");
                                    instanciaTarefa.setDataAlvoFim(alvo);
                                    mbpmBean.getPersistenceService().commitTransaction();
                                }
                            }
                        }
                    });
        }

        for (ProcessDefinition<?> definicaoProcessoMBPM : mbpmBean.getDefinitions()) {
            for (MTask<?> task : definicaoProcessoMBPM.getFlowMap().getTasks()) {
                List<ConditionalTaskAction> acoesAutomaticas = task.getAutomaticActions();
                if (!acoesAutomaticas.isEmpty()) {
                    for (ProcessInstance instancia : definicaoProcessoMBPM.getInstanciasNoEstado(task)) {
                        TaskInstance instanciaTarefa = instancia.getTarefaAtual();
                        for (ConditionalTaskAction acao : acoesAutomaticas) {
                            if (acao.getCondition().test(instanciaTarefa)) {
                                log.append(instancia.getFullId()).append(": Condicao Atingida '")
                                        .append(acao.getCondition().getDescription(instanciaTarefa)).append("' execudando '")
                                        .append(acao.getCompleteDescription()).append("'\n");
                                acao.execute(instanciaTarefa);
                                mbpmBean.getPersistenceService().commitTransaction();
                                break;
                            }
                        }
                    }
                }

            }
        }
        return log.toString();
    }

}
