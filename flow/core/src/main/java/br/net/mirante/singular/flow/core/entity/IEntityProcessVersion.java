package br.net.mirante.singular.flow.core.entity;

import java.util.Date;
import java.util.List;

public interface IEntityProcessVersion extends IEntityByCod {

    IEntityProcessDefinition getProcessDefinition();

    Date getVersionDate();

    void setVersionDate(Date date);

    // TODO Renomear para getVersionTasks();
    @Deprecated
    List<? extends IEntityTaskVersion> getTasks();

    default String getDefinitionClassName() {
        return getProcessDefinition().getDefinitionClassName();
    }

    default String getAbbreviation() {
        return getProcessDefinition().getAbbreviation();
    }

    default String getName() {
        return getProcessDefinition().getName();
    }

    default IEntityCategory getCategory() {
        return getProcessDefinition().getCategory();
    }

    default IEntityTaskVersion getTaskVersion(String abbreviation) {
        for (IEntityTaskVersion situacao : getTasks()) {
            if (situacao.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return situacao;
            }
        }
        return null;
    }
}
