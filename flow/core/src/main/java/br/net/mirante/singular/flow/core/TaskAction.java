package br.net.mirante.singular.flow.core;

import java.io.Serializable;

public abstract class TaskAction implements Serializable {

    public abstract void execute(TaskInstance taskInstance);

    public abstract String getName();

    public String getCompleteDescription() {
        return getName();
    }

}
