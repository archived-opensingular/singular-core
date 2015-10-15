package br.net.mirante.singular.form.mform.function;

import java.util.function.Function;

import br.net.mirante.singular.form.mform.MInstancia;

@FunctionalInterface
public interface InstanciaFunction<I extends MInstancia, R> extends Function<I, R> {

}
