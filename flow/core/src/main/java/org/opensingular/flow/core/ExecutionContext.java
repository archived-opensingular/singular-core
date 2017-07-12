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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExecutionContext<K extends FlowInstance> {

    private final K processInstance;

    private final TaskInstance taskInstance;

    private final VarInstanceMap<?,?> input;

    private STransition transition;

    public ExecutionContext(K processInstance, TaskInstance taskInstance, VarInstanceMap<?,?> input) {
        this(processInstance, taskInstance, input, null);
    }

    public ExecutionContext(K processInstance, TaskInstance taskInstance, VarInstanceMap<?,?> input, STransition transition) {
        this.processInstance = processInstance;
        this.taskInstance = taskInstance;
        this.input = input == null ? VarInstanceMap.empty() : input;
        this.transition = transition;
    }

    public ExecutionContext(TaskInstance taskInstance, VarInstanceMap<?,?> input) {
        this((K)taskInstance.getFlowInstance(), taskInstance, input);
    }

    public K getProcessInstance() {
        return processInstance;
    }

    public TaskInstance getTaskInstance() {
        if (taskInstance == null) {
            return processInstance.getCurrentTaskOrException();
        }
        return taskInstance;
    }

    @Nullable
    public STransition getTransition() {
        return transition;
    }

    public boolean isTransition(@Nonnull String transitionName) {
        return transition != null && transition.getName().equals(transitionName);
    }


    /**
     * Define a transição a ser executada na sequencia. Dispara exception senão exisite a transição com o nome informado
     * na tarefa atual.
     */
    public void setTransition(@Nullable String transitionName) {
        if (transitionName == null) {
            this.transition = null;
        } else {
            this.transition = getTaskInstance().getFlowTaskOrException().getTransitionOrException(transitionName);
        }
    }

    public VarInstanceMap<?,?> getInput() {
        return input;
    }

}
