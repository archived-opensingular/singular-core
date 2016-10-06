package org.opensingular.singular.commons.lambda;

import java.io.Serializable;

public interface ISupplierEx<T, EX extends Exception> extends Serializable {
    T get() throws EX;
}
