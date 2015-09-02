package br.net.mirante.singular.flow.core.entity;

import java.util.List;

public interface IEntityTaskDefinition extends IEntityByCod {

    IEntityProcessDefinition getProcessDefinition();

    String getAbbreviation();
    
    List<? extends IEntityTask> getVersions();
}
