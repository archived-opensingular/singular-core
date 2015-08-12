package br.net.mirante.singular.flow.core;

import java.util.List;

import br.net.mirante.singular.flow.core.entity.IEntityTaskHistoric;

public final class TaskHistoricLog {

    private final IEntityTaskHistoric historic;

    public TaskHistoricLog(IEntityTaskHistoric historico) {
        this.historic = historico;
    }

    public void sendEmail() {
        sendEmail(null);
    }

    public void sendEmail(List<MUser> users) {
        MBPM.getNotifiers().notifyLogToUsers(this, users);
    }

    public ProcessInstance getProcessInstance() {
        return MBPM.getProcessInstance(historic.getTarefa().getDemanda());
    }

    public MUser getResponsibleUser() {
        return historic.getPessoaAlocadora();
    }

    public MUser getAllocatedUser() {
        return historic.getPessoaAlocada();
    }

    public String getTypeDescription() {
        return historic.getDescricaoTipo();
    }

    public String getDetail() {
        return historic.getTextoDetalhamento();
    }
}