package br.net.mirante.singular;

import br.net.mirante.singular.commons.util.log.Loggable;
import br.net.mirante.singular.flow.core.AbstractProcessNotifiers;
import br.net.mirante.singular.flow.core.ExecucaoMTask;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.TaskHistoricLog;
import br.net.mirante.singular.flow.core.TaskInstance;

import java.util.List;

public class NullNotifier extends AbstractProcessNotifiers implements Loggable {
    @Override
    public void notifyUserTaskRelocation(TaskInstance taskInstance, MUser responsibleUser, MUser userToNotify, MUser allocatedUser, MUser removedUser) {

    }

    @Override
    public void notifyUserTaskAllocation(TaskInstance taskInstance, MUser responsibleUser, MUser userToNotify, MUser allocatedUser, MUser removedUser, String justification) {

    }

    @Override
    public void notifyStartToResponsibleUser(TaskInstance taskInstance, ExecucaoMTask execucaoTask) {

    }

    @Override
    public void notifyStartToInterestedUser(TaskInstance taskInstance, ExecucaoMTask execucaoTask) {

    }

    @Override
    public <X extends MUser> void notifyLogToUsers(TaskHistoricLog taskHistoricLog, List<X> usersToNotify) {

    }
}
