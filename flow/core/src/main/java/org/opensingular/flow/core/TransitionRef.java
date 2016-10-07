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
import org.opensingular.flow.core.variable.ValidationResult;

public class TransitionRef {

    private ProcessInstance processInstance;

    private TaskInstance taskInstance;

    private final MTransition transition;

    public TransitionRef(ProcessInstance processInstance, MTransition transition) {
        this.processInstance = processInstance;
        this.transition = transition;
    }

    public TransitionRef(TaskInstance taskInstance, MTransition transition) {
        this.taskInstance = taskInstance;
        this.transition = transition;
    }

    public ProcessInstance getProcessInstance() {
        if (processInstance == null) {
            processInstance = taskInstance.getProcessInstance();
        }
        return processInstance;
    }

    public TaskInstance getTaskInstance() {
        if (taskInstance == null) {
            return processInstance.getCurrentTask();
        }
        return taskInstance;
    }

    public MTransition getTransition() {
        return transition;
    }

    public VarInstanceMap<?> newTransationParameters() {
        return transition.newTransationParameters(this);
    }

    public ValidationResult validate(VarInstanceMap<?> transitionParameters) {
        return transition.validate(this, transitionParameters);
    }
}
