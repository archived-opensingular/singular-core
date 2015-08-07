package br.net.mirante.singular.ui.mform.function;

import br.net.mirante.singular.ui.mform.MInstancia;

@FunctionalInterface
public interface IComportamento<T extends MInstancia> {

    public void on(T instancia);
}
