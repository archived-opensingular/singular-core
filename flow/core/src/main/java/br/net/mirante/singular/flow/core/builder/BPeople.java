package br.net.mirante.singular.flow.core.builder;

import br.net.mirante.singular.flow.core.MBPM;
import br.net.mirante.singular.flow.core.MTaskPeople;

public interface BPeople<SELF extends BPeople<SELF>> extends BExecutavel<SELF, MTaskPeople> {

    public default SELF notifyStartToResponsibleUser() {
        return addStartedTaskListener((instanciaTarefa, execucaoTask) -> MBPM.getNotificadores().notifyStartToResponsibleUser(instanciaTarefa, execucaoTask));
    }

    public default SELF notifyStartToInterestedUser() {
        return addStartedTaskListener((instanciaTarefa, execucaoTask) -> MBPM.getNotificadores().notifyStartToInterestedUser(instanciaTarefa, execucaoTask));
    }
}