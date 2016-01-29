package br.net.mirante.singular.pet.module.exception;

import br.net.mirante.singular.commons.base.SingularException;


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
