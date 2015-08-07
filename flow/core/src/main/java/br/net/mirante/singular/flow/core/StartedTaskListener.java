package br.net.mirante.singular.flow.core;

import java.io.Serializable;

@FunctionalInterface
public interface StartedTaskListener extends Serializable {

    public void onTaskStart(TaskInstance taskIntance, ExecucaoMTask execucaoTask);
}
