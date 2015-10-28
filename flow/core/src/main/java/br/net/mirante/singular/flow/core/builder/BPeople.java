package br.net.mirante.singular.flow.core.builder;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MTaskPeople;

public interface BPeople<SELF extends BPeople<SELF>> extends BUserExecutable<SELF, MTaskPeople> {

    public default SELF notifyStartToResponsibleUser() {
        return addStartedTaskListener((instanciaTarefa, execucaoTask) -> Flow.notifyListeners(n -> n.notifyStartToResponsibleUser(instanciaTarefa, execucaoTask)));
    }

    public default SELF notifyStartToInterestedUser() {
        return addStartedTaskListener((instanciaTarefa, execucaoTask) -> Flow.notifyListeners(n -> n.notifyStartToInterestedUser(instanciaTarefa, execucaoTask)));
    }
}