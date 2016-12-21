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

import org.opensingular.flow.schedule.IScheduleData;
import org.opensingular.flow.schedule.IScheduledJob;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

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

        final SingularFlowConfigurationBean mbpmBean = Flow.getConfigBean();
        final StringBuilder log = new StringBuilder();
        final Date today = new Date();

        for (ProcessDefinition<?> definicaoProcessoMBPM : mbpmBean.getDefinitions()) {
            for (final MTaskWait task : definicaoProcessoMBPM.getFlowMap().getWaitTasks()) {
                executeTaskIfNecessary(mbpmBean, log, today, definicaoProcessoMBPM, task);
            }
        }

        for (ProcessDefinition<?> definicaoProcessoMBPM : mbpmBean.getDefinitions()) {
            definicaoProcessoMBPM.getFlowMap().getPeopleTasks().stream()
                    .filter(task -> task.getTargetDateExecutionStrategy() != null)
                    .forEach(fillTargetEndDate(mbpmBean, log, definicaoProcessoMBPM));
        }

        for (ProcessDefinition<?> definicaoProcessoMBPM : mbpmBean.getDefinitions()) {
            for (MTask<?> task : definicaoProcessoMBPM.getFlowMap().getTasks()) {
                List<IConditionalTaskAction> acoesAutomaticas = task.getAutomaticActions();
                if (!acoesAutomaticas.isEmpty()) {
                    executeAutomaticActions(mbpmBean, log, definicaoProcessoMBPM, task, acoesAutomaticas);
                }

            }
        }

        return log.toString();
    }

    private void executeTaskIfNecessary(SingularFlowConfigurationBean mbpmBean, StringBuilder log, Date hoje,
                                        ProcessDefinition<?> definicaoProcessoMBPM, MTaskWait task) {
        if (task.hasExecutionDateStrategy()) {
            for (ProcessInstance instancia : definicaoProcessoMBPM.getDataService().retrieveAllInstancesIn(task)) {
                TaskInstance instanciaTarefa = instancia.getCurrentTask();
                Date         dataExecucao    = task.getExecutionDate(instancia, instanciaTarefa);
                if (!dataExecucao.equals(instancia.getCurrentTask().getTargetEndDate())) {
                    instancia.getCurrentTask().setTargetEndDate(dataExecucao);
                }
                if (hoje.after(dataExecucao)) {
                    log.append("Executando transição da instância: ").append(instancia.getFullId()).append("\n");
                    instancia.executeTransition();
                    mbpmBean.getPersistenceService().commitTransaction();
                }

            }
        }
    }

    private Consumer<MTaskPeople> fillTargetEndDate(SingularFlowConfigurationBean mbpmBean, StringBuilder log, ProcessDefinition<?> definicaoProcessoMBPM) {
        return task -> {
            // Preenche Data Alvo para os casos que estiverem null
            for (ProcessInstance instancia : definicaoProcessoMBPM.getDataService().retrieveAllInstancesIn(task)) {
                TaskInstance instanciaTarefa = instancia.getCurrentTask();
                if (instanciaTarefa.getTargetEndDate() == null) {
                    Date alvo = task.getTargetDateExecutionStrategy().apply(instancia, instanciaTarefa);
                    if (alvo != null) {
                        log.append("Alterando data alvo: ").append(instancia.getFullId()).append(" para ").append(alvo)
                                .append("\n");
                        instanciaTarefa.setTargetEndDate(alvo);
                        mbpmBean.getPersistenceService().commitTransaction();
                    }
                }
            }
        };
    }

    private void executeAutomaticActions(SingularFlowConfigurationBean mbpmBean, StringBuilder log, ProcessDefinition<?> definicaoProcessoMBPM, MTask<?> task, List<IConditionalTaskAction> acoesAutomaticas) {
        for (ProcessInstance instancia : definicaoProcessoMBPM.getDataService().retrieveAllInstancesIn(task)) {
            TaskInstance instanciaTarefa = instancia.getCurrentTask();
            for (IConditionalTaskAction acao : acoesAutomaticas) {
                if (acao.getPredicate().test(instanciaTarefa)) {
                    log.append(instancia.getFullId()).append(": Condicao Atingida '")
                            .append(acao.getPredicate().getDescription(instanciaTarefa)).append("' execudando '")
                            .append(acao.getCompleteDescription()).append("'\n");
                    acao.execute(instanciaTarefa);
                    mbpmBean.getPersistenceService().commitTransaction();
                    break;
                }
            }
        }
    }

}
