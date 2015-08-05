package br.net.mirante.mform.function;

import java.util.function.Function;

import br.net.mirante.mform.MInstancia;

@FunctionalInterface
public interface InstanciaFunction<I extends MInstancia, R> extends Function<I, R> {

}
