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

package org.opensingular.flow.core;

import org.opensingular.flow.core.entity.IEntityTaskInstanceHistory;

import java.util.List;

public final class TaskHistoricLog {

    private final IEntityTaskInstanceHistory historic;

    public TaskHistoricLog(IEntityTaskInstanceHistory historico) {
        this.historic = historico;
    }

    public void sendEmail() {
        sendEmail(null);
    }

    public void sendEmail(List<MUser> users) {
        Flow.notifyListeners(n -> n.notifyLogToUsers(this, users));
    }

    public ProcessInstance getProcessInstance() {
        return Flow.getProcessInstance(historic.getTaskInstance().getProcessInstance());
    }

    public MUser getAllocatorUser() {
        return historic.getAllocatorUser();
    }

    public MUser getAllocatedUser() {
        return historic.getAllocatedUser();
    }

    public String getTypeDescription() {
        return historic.getType().getDescription();
    }

    public String getDescription() {
        return historic.getDescription();
    }
}