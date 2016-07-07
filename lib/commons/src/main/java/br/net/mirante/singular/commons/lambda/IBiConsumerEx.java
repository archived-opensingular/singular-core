package br.net.mirante.singular.commons.lambda;

import java.io.Serializable;

public interface IBiConsumerEx<T, U, EX extends Exception> extends Serializable {
    void accept(T t, U u) throws EX;
}
