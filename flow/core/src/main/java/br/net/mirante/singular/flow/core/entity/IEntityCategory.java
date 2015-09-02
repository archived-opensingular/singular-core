package br.net.mirante.singular.flow.core.entity;

import java.util.List;

public interface IEntityCategory extends IEntityByCod {

    String getName();

    void setName(String nome);

    IEntityCategory getParent();

    List<? extends IEntityCategory> getChildrens();

    List<? extends IEntityProcessDefinition> getProcessDefinitions();
}
