package br.net.mirante.singular.flow.core.entity;

import java.util.List;

public interface IEntityCategory extends IEntityByCod {

    String getName();

    List<? extends IEntityProcessDefinition> getProcessDefinitions();
}
