package br.net.mirante.singular.flow.core.entity;

import java.util.Date;
import java.util.List;

public interface IEntityProcessVersion extends IEntityByCod {

    IEntityProcessDefinition getProcessDefinition();

    Date getVersionDate();

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

    default IEntityTaskVersion getTask(String abbreviation) {
        for (IEntityTaskVersion situacao : getTasks()) {
            if (situacao.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return situacao;
            }
        }
        return null;
    }

    default IEntityTaskDefinition getTaskDefinition(String abbreviation) {
        return getProcessDefinition().getTaskDefinition(abbreviation);
    }
    
    default <X extends IEntityProcessRole> X getRole(String abbreviation) {
        return getProcessDefinition().getRole(abbreviation);
    }
}
