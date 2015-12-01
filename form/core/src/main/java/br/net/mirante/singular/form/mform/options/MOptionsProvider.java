package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;

public interface MOptionsProvider {

    public abstract String toDebug();

    public MILista<? extends MInstancia> getOpcoes(MInstancia optionsInstance);
}
