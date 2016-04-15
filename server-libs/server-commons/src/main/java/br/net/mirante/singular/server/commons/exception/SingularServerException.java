package br.net.mirante.singular.server.commons.exception;

import br.net.mirante.singular.commons.base.SingularException;

/**
 * Exceções do Singular pet server e seus módulos
 */
public class SingularServerException extends SingularException {

    public SingularServerException(String msg) {
        super(msg);
    }

    public SingularServerException(Throwable cause) {
        super(cause);
    }

    public SingularServerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
