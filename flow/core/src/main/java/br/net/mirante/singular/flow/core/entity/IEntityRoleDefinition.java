package br.net.mirante.singular.flow.core.entity;


public interface IEntityRoleDefinition extends IEntityByCod<Integer> {

    String getAbbreviation();

    void setAbbreviation(String abbreviation);

    String getName();

    void setName(String name);

    IEntityProcessDefinition getProcessDefinition();

    void setProcessDefinition(IEntityProcessDefinition processDefinition);
}
