package br.net.mirante.singular.flow.core;

import br.net.mirante.singular.flow.util.vars.VarInstanceMap;

public class ExecutionContext {

    private final ProcessInstance processInstance;

    private final TaskInstance taskInstance;

    private final VarInstanceMap<?> input;

    private String transition;

    public ExecutionContext(ProcessInstance processInstance, TaskInstance taskInstance, VarInstanceMap<?> input) {
        this.processInstance = processInstance;
        this.taskInstance = taskInstance;
        this.input = input == null ? VarInstanceMap.empty() : input;
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
