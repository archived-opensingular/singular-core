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

public interface IEntityTaskDefinition extends IEntityByCod<Integer> {

    IEntityFlowDefinition getFlowDefinition();

    String getAbbreviation();

    void setAbbreviation(String abbreviation);

    List<? extends IEntityTaskVersion> getVersions();

    AccessStrategyType getAccessStrategyType();

    void setAccessStrategyType(AccessStrategyType accessStrategyType);

    List<? extends IEntityRoleTask> getRolesTask();

    default String getName(){
        return getLastVersion().getName();
    }

    default IEntityTaskVersion getLastVersion(){
        return getVersions().stream().collect(Collectors.maxBy(Comparator.comparing(IEntityTaskVersion::getVersionDate))).get();
    }
}
