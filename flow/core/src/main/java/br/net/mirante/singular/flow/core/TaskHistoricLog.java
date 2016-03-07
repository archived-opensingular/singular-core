package br.net.mirante.singular.flow.core;

import br.net.mirante.singular.flow.core.entity.IEntityTaskInstanceHistory;

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