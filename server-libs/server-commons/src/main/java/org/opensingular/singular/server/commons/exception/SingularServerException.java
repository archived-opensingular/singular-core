package org.opensingular.singular.server.commons.exception;

import org.opensingular.lib.commons.base.SingularException;

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
