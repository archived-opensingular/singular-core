package br.net.mirante.singular.flow.core.entity;

import java.util.List;

public interface IEntityProcessRole extends IEntityByCod {

    String getAbbreviation();

    void setAbbreviation(String abbreviation);

    String getName();

    void setName(String name);

    IEntityProcessDefinition getProcessDefinition();

    void setProcessDefinition(IEntityProcessDefinition processDefinition);

    @Deprecated
    // TODO lista muito pessada. Trocar uso por um consulta especifica e apagar
    // essa lista assim que o código que usa for refatorado
    List<? extends IEntityRole> getRolesInstances();

    @Deprecated
    void setRolesInstancesAsEmpty();
}
