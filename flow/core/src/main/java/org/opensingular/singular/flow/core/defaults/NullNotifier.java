/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.defaults;

import org.opensingular.singular.flow.core.ProcessNotifier;
import org.opensingular.singular.flow.core.ExecutionContext;
import org.opensingular.singular.flow.core.MUser;
import org.opensingular.singular.flow.core.ProcessInstance;
import org.opensingular.singular.flow.core.TaskHistoricLog;
import org.opensingular.singular.flow.core.TaskInstance;

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
