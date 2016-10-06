/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
