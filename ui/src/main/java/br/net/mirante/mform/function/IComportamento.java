package br.net.mirante.mform.function;

import br.net.mirante.mform.MInstancia;

@FunctionalInterface
public interface IComportamento<T extends MInstancia> {

    public void on(T instancia);
}
