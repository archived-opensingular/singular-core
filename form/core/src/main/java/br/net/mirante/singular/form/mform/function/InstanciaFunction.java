package br.net.mirante.singular.form.mform.function;

import java.util.function.Function;

import br.net.mirante.singular.form.mform.SInstance2;

@FunctionalInterface
public interface InstanciaFunction<I extends SInstance2, R> extends Function<I, R> {

}
