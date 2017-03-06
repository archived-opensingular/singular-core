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

import org.opensingular.flow.core.MUser;

import java.util.Date;
import java.util.List;

public interface IEntityTaskInstance extends IEntityByCod<Integer> {

    IEntityProcessInstance getProcessInstance();

    IEntityTaskVersion getTaskVersion();

    Date getBeginDate();

    void setBeginDate(Date begin);

    Date getEndDate();

    void setEndDate(Date end);

    Date getTargetEndDate();

    void setVersionStamp(Integer v);

    Integer getVersionStamp();

    void setTargetEndDate(Date targetEndDate);

    void setAllocatedUser(MUser allocatedUser);

    MUser getAllocatedUser();

    void setResponsibleUser(MUser responsibleUser);

    MUser getResponsibleUser();

    IEntityTaskTransitionVersion getExecutedTransition();

    void setExecutedTransition(IEntityTaskTransitionVersion transition);

    List<? extends IEntityExecutionVariable> getInputVariables();

    List<? extends IEntityExecutionVariable> getOutputVariables();

    List<? extends IEntityTaskInstanceHistory> getTaskHistory();

    List<? extends IEntityProcessInstance> getChildProcesses();

    default boolean isActive() {
        return getEndDate() == null
                || getTaskVersion().getType().isEnd();
    }

    default boolean isFinished() {
        return getEndDate() != null;
    }
}
