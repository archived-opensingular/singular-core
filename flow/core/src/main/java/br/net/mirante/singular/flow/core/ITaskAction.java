package br.net.mirante.singular.flow.core;


public interface ITaskAction {

    void execute(TaskInstance taskInstance);

    String getName();

    default String getCompleteDescription() {
        return getName();
    }

}
