package br.net.mirante.singular.flow.core;

public interface IConditionalTaskAction extends ITaskAction {

    ITaskPredicate getPredicate();

}
