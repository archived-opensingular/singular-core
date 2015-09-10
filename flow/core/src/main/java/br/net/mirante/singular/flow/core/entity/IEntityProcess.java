package br.net.mirante.singular.flow.core.entity;

import java.util.Date;
import java.util.List;

public interface IEntityProcess extends IEntityByCod {

    IEntityProcessDefinition getProcessDefinition();

    Date getVersionDate();

    List<? extends IEntityTask> getTasks();
    
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

    default IEntityTask getTask(String abbreviation) {
        for (IEntityTask situacao : getTasks()) {
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
