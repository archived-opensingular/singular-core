/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.entity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface IEntityProcessDefinition extends IEntityByCod<Integer> {

    String getKey();

    void setKey(String abbreviation);

    String getName();

    void setName(String name);

    String getDefinitionClassName();

    void setDefinitionClassName(String name);

    IEntityCategory getCategory();

    void setCategory(IEntityCategory category);

    IEntityProcessGroup getProcessGroup();

    void setProcessGroup(IEntityProcessGroup processGroup);

    List<? extends IEntityTaskDefinition> getTaskDefinitions();

    List<? extends IEntityRoleDefinition> getRoles();

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
    default <X extends IEntityRoleDefinition> X getRole(String abbreviation) {
        for (IEntityRoleDefinition papel : getRoles()) {
            if (papel.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return (X) papel;
            }
        }
        return null;
    }

}
