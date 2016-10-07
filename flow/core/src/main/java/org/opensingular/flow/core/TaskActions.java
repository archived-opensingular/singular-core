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

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

public class TaskActions {

    public static IConditionalTaskAction executeTransition(final ITaskPredicate predicate, final MTransition transicao) {
        return executeTransition(predicate, transicao.getName());
    }

    public static IConditionalTaskAction executeTransition(final ITaskPredicate predicate, final String destinationName) {
        TaskActionImpl executeTransition = new TaskActionImpl("Executar Transicao", taskInstance ->{
            taskInstance.log("Transição Automática", "motivo: " + predicate.getDescription(taskInstance));
            taskInstance.getProcessInstance().executeTransition(destinationName);
        });
        executeTransition.setCompleteDescription("Executar Transicao '" + destinationName + "'");
        
        return conditionalAction(predicate, executeTransition);
    }

    public static IConditionalTaskAction conditionalAction(ITaskPredicate predicate, ITaskAction action){
        return new ConditionalTaskActionImpl(predicate, action);
    }
    
    static class TaskActionImpl implements ITaskAction{
        private final String name;
        private final Consumer<TaskInstance> action;
        private String completeDescription;
        
        public TaskActionImpl(String name, Consumer<TaskInstance> action) {
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
        private final ITaskAction action;

        ConditionalTaskActionImpl(ITaskPredicate predicate, ITaskAction action) {
            this.predicate = predicate;
            this.action = action;
        }

        @Override
        public ITaskPredicate getPredicate() {
            return predicate;
        }

        @Override
        public void execute(TaskInstance instanciaTarefa) {
            action.execute(instanciaTarefa);
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
