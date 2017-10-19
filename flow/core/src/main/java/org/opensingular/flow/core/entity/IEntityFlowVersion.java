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

import java.util.Date;
import java.util.List;

public interface IEntityFlowVersion extends IEntityByCod<Integer> {

    IEntityFlowDefinition getFlowDefinition();

    Date getVersionDate();

    void setVersionDate(Date date);

    List<? extends IEntityTaskVersion> getVersionTasks();

    default String getDefinitionClassName() {
        return getFlowDefinition().getDefinitionClassName();
    }

    default String getAbbreviation() {
        return getFlowDefinition().getKey();
    }

    default String getName() {
        return getFlowDefinition().getName();
    }

    default IEntityCategory getCategory() {
        return getFlowDefinition().getCategory();
    }

    default IEntityTaskVersion getTaskVersion(String abbreviation) {
        for (IEntityTaskVersion version : getVersionTasks()) {
            if (version.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return version;
            }
        }
        return null;
    }
}
