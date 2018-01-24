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

import org.opensingular.flow.core.SUser;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IEntityFlowInstance extends IEntityByCod<Integer> {

    IEntityFlowVersion getFlowVersion();

    String getDescription();

    void setDescription(String description);

    SUser getUserCreator();

    @Nonnull
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
        for (IEntityRoleInstance entityRoleInstance : getRoles()) {
            if (roleAbbreviation.equalsIgnoreCase(entityRoleInstance.getRole().getAbbreviation())) {
                return entityRoleInstance;
            }
        }
        return null;
    }

    default IEntityVariableInstance getVariable(String ref) {
        return getVariables().stream().filter(var -> var.getName().equalsIgnoreCase(ref)).findAny().orElse(null);
    }

    /**
     * This method should be used only with forked instances, otherwise it will always return a single element list.
     * If not forked use {@link #getCurrentTask()} instead.
     *
     * @return a list of active tasks.
     */
    default List<IEntityTaskInstance> getCurrentTasks() {
        List<IEntityTaskInstance>           currentTasks = new ArrayList<>();
        List<? extends IEntityTaskInstance> list         = getTasks();
        for (int i = list.size() - 1; i != -1; i--) {
            IEntityTaskInstance task = list.get(i);
            if (task.isActive()) {
                currentTasks.add(task);
            }
        }
        return currentTasks;
    }


    /**
     * Returns the current task ie: the most recent task (ordered by begin date) that is currently active according
     * to {@link IEntityTaskInstance#isActive()} method.
     *
     * @return
     */
    @Nonnull
    default Optional<IEntityTaskInstance> getCurrentTask() {
        List<? extends IEntityTaskInstance> list = getTasks();
        for (int i = list.size() - 1; i != -1; i--) {
            IEntityTaskInstance task = list.get(i);
            if (task.isActive()) {
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }

}
