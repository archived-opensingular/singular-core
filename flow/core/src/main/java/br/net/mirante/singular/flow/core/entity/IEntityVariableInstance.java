package br.net.mirante.singular.flow.core.entity;

public interface IEntityVariableInstance extends IEntityByCod<Integer> {

    IEntityVariableType getType();

    void setType(IEntityVariableType type);

    String getName();

    String getValue();

    void setValue(String value);

    IEntityProcessInstance getProcessInstance();

}
