package br.net.mirante.singular.flow.core;

@FunctionalInterface
public interface ListenerExecucaoTransicao {

    public void onTransicaoPosExecucacao(TaskInstance instanciaTarefa, MTransition transicao);
}
