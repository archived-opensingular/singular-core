package br.net.mirante.singular.flow.core;

import java.util.List;

public abstract class AbstractProcessNotifiers {

    //TODO sugestão: responsibleUser -> userInCharge
    public abstract void notifyUserTaskRelocation(TaskInstance taskInstance, MUser responsibleUser, MUser userToNotify, MUser allocatedUser,
            MUser removedUser);

    //TODO sugestão: responsibleUser -> userInCharge
    public abstract void notifyUserTaskAllocation(TaskInstance taskInstance, MUser responsibleUser, MUser userToNotify, MUser allocatedUser,
            MUser removedUser, String justification);

    public abstract void notifyStartToResponsibleUser(TaskInstance taskInstance, ExecucaoMTask execucaoTask);

    public abstract void notifyStartToInterestedUser(TaskInstance taskInstance, ExecucaoMTask execucaoTask);

    public abstract <X extends MUser> void notifyLogToUsers(TaskHistoricLog taskHistoricLog, List<X> usersToNotify);
}
