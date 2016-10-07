/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.core.entity;

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
