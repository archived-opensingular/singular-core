package org.opensingular.singular.commons.lambda;

import java.io.Serializable;

public interface IConsumerEx<T, EX extends Exception> extends Serializable {
    void accept(T t) throws EX;
}
