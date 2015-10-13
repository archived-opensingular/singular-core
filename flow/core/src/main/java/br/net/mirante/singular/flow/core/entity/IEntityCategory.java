package br.net.mirante.singular.flow.core.entity;

import java.util.List;

public interface IEntityCategory extends IEntityByCod {

    String getName();

    void setName(String name);

    List<? extends IEntityProcessDefinition> getProcessDefinitions();
}
