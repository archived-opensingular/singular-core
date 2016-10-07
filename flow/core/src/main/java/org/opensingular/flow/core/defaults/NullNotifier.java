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

package org.opensingular.flow.core.defaults;

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.ProcessNotifier;
import org.opensingular.flow.core.ExecutionContext;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.TaskHistoricLog;
import org.opensingular.flow.core.TaskInstance;

import java.util.List;

public class NullNotifier implements ProcessNotifier {

    @Override
    public void notifyUserTaskRelocation(TaskInstance taskInstance, MUser responsibleUser, MUser userToNotify, MUser allocatedUser, MUser removedUser) {

    }

    @Override
    public void notifyUserTaskAllocation(TaskInstance taskInstance, MUser responsibleUser, MUser userToNotify, MUser allocatedUser, MUser removedUser, String justification) {

    }

    @Override
    public void notifyStartToResponsibleUser(TaskInstance taskInstance, ExecutionContext execucaoTask) {

    }

    @Override
    public void notifyStartToInterestedUser(TaskInstance taskInstance, ExecutionContext execucaoTask) {

    }

    @Override
    public <X extends MUser> void notifyLogToUsers(TaskHistoricLog taskHistoricLog, List<X> usersToNotify) {

    }

    @Override
    public void notifyStateUpdate(ProcessInstance instanciaProcessoMBPM) {

    }

}
