package br.net.mirante.singular.flow.core;

public abstract class ConditionalTaskAction extends TaskAction {

    public abstract TaskPredicate getCondition();

}
