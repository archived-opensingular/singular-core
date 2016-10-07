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

import org.opensingular.flow.core.variable.VarInstanceMap;

public class ExecutionContext {

    private final ProcessInstance processInstance;

    private final TaskInstance taskInstance;

    private final VarInstanceMap<?> input;

    private String transition;

    public ExecutionContext(ProcessInstance processInstance, TaskInstance taskInstance, VarInstanceMap<?> input) {
        this(processInstance, taskInstance, input, null);
    }

    public ExecutionContext(ProcessInstance processInstance, TaskInstance taskInstance, VarInstanceMap<?> input, MTransition transition) {
        this.processInstance = processInstance;
        this.taskInstance = taskInstance;
        this.input = input == null ? VarInstanceMap.empty() : input;
        this.transition = transition != null ? transition.getName() : null;
    }

    public ExecutionContext(TaskInstance taskInstance, VarInstanceMap<?> input) {
        this(taskInstance.getProcessInstance(), taskInstance, input);
    }

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public TaskInstance getTaskInstance() {
        if (taskInstance == null) {
            return processInstance.getCurrentTask();
        }
        return taskInstance;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public VarInstanceMap<?> getInput() {
        return input;
    }

}
