package br.net.mirante.singular.flow.core.builder;

import br.net.mirante.singular.flow.core.MBPM;
import br.net.mirante.singular.flow.core.MTaskPeople;

public interface BPeople<SELF extends BPeople<SELF>> extends BUserExecutable<SELF, MTaskPeople> {

    public default SELF notifyStartToResponsibleUser() {
        return addStartedTaskListener((instanciaTarefa, execucaoTask) -> MBPM.getNotifiers().notifyStartToResponsibleUser(instanciaTarefa, execucaoTask));
    }

    public default SELF notifyStartToInterestedUser() {
        return addStartedTaskListener((instanciaTarefa, execucaoTask) -> MBPM.getNotifiers().notifyStartToInterestedUser(instanciaTarefa, execucaoTask));
    }
}