package br.net.mirante.singular.form.mform;

public interface MProviderOpcoes {

    public abstract String toDebug();

    public MILista<? extends MInstancia> getOpcoes();
}
