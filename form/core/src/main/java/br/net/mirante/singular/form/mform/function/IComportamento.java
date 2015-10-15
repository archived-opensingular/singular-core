package br.net.mirante.singular.form.mform.function;

import br.net.mirante.singular.form.mform.MInstancia;

@FunctionalInterface
public interface IComportamento<T extends MInstancia> {

    public void on(T instancia);
}
