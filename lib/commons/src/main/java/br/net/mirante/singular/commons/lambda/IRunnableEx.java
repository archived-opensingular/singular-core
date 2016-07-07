package br.net.mirante.singular.commons.lambda;

import java.io.Serializable;

public interface IRunnableEx<EX extends Exception> extends Serializable {
    void run() throws EX;
}
