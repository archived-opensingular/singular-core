package br.net.mirante.singular.flow.core;

import java.util.List;

public abstract class AbstractNotificadores {

    public abstract void notificarDesalocacaoTarefa(TaskInstance instanciaTarefa, MUser autor, MUser userDestino, MUser userAlocada,
            MUser userDesalocada);

    public abstract void notificarAlocacaoTarefa(TaskInstance instanciaTarefa, MUser autor, MUser userDestino, MUser userAlocada,
            MUser userDesalocada, String textoExplicacaoAlocacao);

    public abstract void notifyStartToResponsibleUser(TaskInstance instanciaTarefa, ExecucaoMTask execucaoTask);

    public abstract void notifyStartToInterestedUser(TaskInstance instanciaTarefa, ExecucaoMTask execucaoTask);

    public abstract <X extends MUser> void notify(TaskHistoricLog logHistorico, List<X> usersNotificar);
}
