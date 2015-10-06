package br.net.mirante.singular.flow.core.entity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface IEntityProcessDefinition extends IEntityByCod {

    String getAbbreviation();

    String getName();

    String getDefinitionClassName();

    IEntityCategory getCategory();

    List<? extends IEntityTaskDefinition> getTaskDefinitions();

    List<? extends IEntityProcessRole> getRoles();

    List<? extends IEntityProcessVersion> getVersions();

    default IEntityProcessVersion getLastVersion(){
        return getVersions().stream().collect(Collectors.maxBy(Comparator.comparing(IEntityProcessVersion::getVersionDate))).orElse(null);
    }

    default IEntityTaskDefinition getTaskDefinition(String sigla) {
        // TODO Daniel - esse método e os demais são muito ineficiente.
        // Particularmente esse é muito usando. A solução para isso seria uma
        // classe derivada que fizesse cache em uma Map.
        for (IEntityTaskDefinition situacao : getTaskDefinitions()) {
            if (situacao.getAbbreviation().equalsIgnoreCase(sigla)) {
                return situacao;
            }
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    default <X extends IEntityProcessRole> X getRole(String abbreviation) {
        for (IEntityProcessRole papel : getRoles()) {
            if (papel.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return (X) papel;
            }
        }
        return null;
    }

}
