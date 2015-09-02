package br.net.mirante.singular.flow.core.entity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface IEntityProcessDefinition extends IEntityByCod {
    
    String getAbbreviation();
    
    void setAbbreviation(String abbreviation);

    String getName();
    
    void setName(String name);
    
    String getDefinitionClassName();

    void setDefinitionClassName(String definitionClassName);
    
    IEntityCategory getCategory();

    List<? extends IEntityTaskDefinition> getTaskDefinitions();

    List<? extends IEntityProcessRole> getRoles();
    
    List<? extends IEntityProcess> getVersions();
    
    default IEntityProcess getLastVersion(){
        return getVersions().stream().collect(Collectors.maxBy(Comparator.comparing(IEntityProcess::getVersionDate))).orElse(null);
    }
    
    default IEntityTaskDefinition getTaskDefinition(String sigla) {
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
