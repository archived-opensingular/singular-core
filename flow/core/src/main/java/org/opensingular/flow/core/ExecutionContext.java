/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
