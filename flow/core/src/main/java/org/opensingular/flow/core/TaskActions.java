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

import org.apache.commons.lang3.StringUtils;

import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.schedule.IScheduleData;

import java.util.Optional;

public class TaskActions {

    private TaskActions() {}

    public static IConditionalTaskAction executeTransition(final ITaskPredicate predicate, final STransition transition) {
        return executeTransition(predicate, transition.getName(), transition.getDestination().getAbbreviation());
    }

    public static IConditionalTaskAction executeTransition(final ITaskPredicate predicate, final String transitionName, final String destinationTaskAbbreviation) {
        TaskActionImpl executeTransition = new TaskActionImpl("Executar Transicao", taskInstance ->{
            taskInstance.log("Transição Automática", "motivo: " + predicate.getDescription(taskInstance));
            taskInstance.prepareTransition(transitionName).go();
        });
        executeTransition.setCompleteDescription("Executar Transicao '" + transitionName + "'");
        
        return conditionalAction(predicate, executeTransition, destinationTaskAbbreviation);
    }

    public static IConditionalTaskAction conditionalAction(ITaskPredicate predicate, ITaskAction action, String destinationTaskAbbreviation){
        return new ConditionalTaskActionImpl(predicate, action, destinationTaskAbbreviation);
    }
    
    static class TaskActionImpl implements ITaskAction{
        private final String                          name;
        private final IConsumer<TaskInstance> action;
        private       String                          completeDescription;
        
        public TaskActionImpl(String name, IConsumer<TaskInstance> action) {
            super();
            this.name = name;
            this.action = action;
        }

        @Override
        public void execute(TaskInstance taskInstance) {
            action.accept(taskInstance);
        }

        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public String getCompleteDescription() {
            return StringUtils.defaultIfBlank(completeDescription, ITaskAction.super.getCompleteDescription());
        }
        
        public void setCompleteDescription(String completeDescription) {
            this.completeDescription = completeDescription;
        }
    }
    
    static class ConditionalTaskActionImpl implements IConditionalTaskAction {

        private final ITaskPredicate predicate;
        private final ITaskAction    action;
        private final String         destinationTaskAbbreviation;
        private       IScheduleData  scheduleData;

        ConditionalTaskActionImpl(ITaskPredicate predicate, ITaskAction action, String destinationTaskAbbreviation) {
            this.predicate = predicate;
            this.action = action;
            this.destinationTaskAbbreviation = destinationTaskAbbreviation;
        }

        @Override
        public Optional<IScheduleData> getScheduleData() {
            return Optional.ofNullable(scheduleData);
        }

        @Override
        public void setScheduleData(IScheduleData scheduleData) {
            this.scheduleData = scheduleData;
        }

        @Override
        public ITaskPredicate getPredicate() {
            return predicate;
        }

        @Override
        public String getDestinationTaskAbbreviation() {
            return destinationTaskAbbreviation;
        }

        @Override
        public void execute(TaskInstance taskInstance) {
            action.execute(taskInstance);
        }

        @Override
        public String getName() {
            return action.getName();
        }

        @Override
        public String getCompleteDescription() {
            return action.getCompleteDescription();
        }

    }
}
