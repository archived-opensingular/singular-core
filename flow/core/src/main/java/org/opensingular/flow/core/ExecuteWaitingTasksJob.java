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

        final SingularFlowConfigurationBean singularFlowConfigurationBean = Flow.getConfigBean();
        final StringBuilder log = new StringBuilder();
        final Date today = new Date();

        for (FlowDefinition<?> definition : singularFlowConfigurationBean.getDefinitions()) {
            definition.getFlowMap().getHumanTasks().stream()
                    .filter(task -> task.getTargetDateExecutionStrategy() != null)
                    .forEach(fillTargetEndDate(singularFlowConfigurationBean, log, definition));
        }

        for (FlowDefinition<?> definition : singularFlowConfigurationBean.getDefinitions()) {
            for (final STaskWait task : definition.getFlowMap().getWaitTasks()) {
                executeTaskIfNecessary(singularFlowConfigurationBean, log, today, definition, task);
            }
        }

        return log.toString();
    }

    private void executeTaskIfNecessary(SingularFlowConfigurationBean singularFlowConfigurationBean, StringBuilder log, Date now,
                                        FlowDefinition<?> flowDefinition, STaskWait task) {
        if (task.hasExecutionDateStrategy()) {
            for (FlowInstance instance : flowDefinition.getDataService().retrieveAllInstancesIn(task)) {
                TaskInstance taskInstance = instance.getCurrentTaskOrException();
                Date         executionDate    = taskInstance.getTargetEndDate();
                if (!executionDate.equals(taskInstance.getTargetEndDate())) {
                    taskInstance.setTargetEndDate(executionDate);
                }
                if (now.after(executionDate)) {
                    log.append("Executando transição da instância: ").append(instance.getFullId()).append('\n');
                    instance.prepareTransition().go();
                    singularFlowConfigurationBean.getPersistenceService().commitTransaction();
                }

            }
        }
    }

    private Consumer<STaskHuman> fillTargetEndDate(SingularFlowConfigurationBean singularFlowConfigurationBean, StringBuilder log, FlowDefinition<?> flowDefinition) {
        return task -> {
            // Preenche Data Alvo para os casos que estiverem null
            for (FlowInstance instance : flowDefinition.getDataService().retrieveAllInstancesIn(task)) {
                TaskInstance taskInstance = instance.getCurrentTaskOrException();
                if (taskInstance.getTargetEndDate() == null) {
                    Date target = task.getTargetDateExecutionStrategy().apply(instance, taskInstance);
                    if (target != null) {
                        log.append("Alterando data alvo: ").append(instance.getFullId()).append(" para ").append(target)
                                .append('\n');
                        taskInstance.setTargetEndDate(target);
                        singularFlowConfigurationBean.getPersistenceService().commitTransaction();
                    }
                }
            }
        };
    }



}
