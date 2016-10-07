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

import org.opensingular.flow.core.MUser;

public interface IEntityProcessInstance extends IEntityByCod<Integer> {

    IEntityProcessVersion getProcessVersion();

    String getDescription();

    void setDescription(String description);

    MUser getUserCreator();

    Date getBeginDate();

    void setBeginDate(Date beginDate);

    Date getEndDate();

    void setEndDate(Date end);

    IEntityTaskInstance getParentTask();

    void setParentTask(IEntityTaskInstance parent);

    void addTask(IEntityTaskInstance taskInstance);

    List<? extends IEntityTaskInstance> getTasks();

    List<? extends IEntityVariableInstance> getVariables();

    List<? extends IEntityExecutionVariable> getHistoricalVariables();

    List<? extends IEntityRoleInstance> getRoles();

    default IEntityRoleInstance getRoleUserByAbbreviation(String roleAbbreviation) {
        for (IEntityRoleInstance dadosPapelInstancia : getRoles()) {
            if (roleAbbreviation.equalsIgnoreCase(dadosPapelInstancia.getRole().getAbbreviation())) {
                return dadosPapelInstancia;
            }
        }
        return null;
    }

    default IEntityVariableInstance getVariable(String ref) {
        return getVariables().stream().filter(var -> var.getName().equalsIgnoreCase(ref)).findAny().orElse(null);
    }
}
