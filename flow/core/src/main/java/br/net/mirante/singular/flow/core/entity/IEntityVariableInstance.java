package br.net.mirante.singular.flow.core.entity;

public interface IEntityVariableInstance extends IEntityByCod {

    IEntityVariableType getType();
    
    String getName();

    String getValue();

    IEntityProcessInstance getProcessInstance();

}
