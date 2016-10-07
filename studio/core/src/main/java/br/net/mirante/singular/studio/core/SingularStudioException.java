package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.commons.base.SingularException;

public class SingularStudioException extends SingularException {
    public SingularStudioException() {
    }

    public SingularStudioException(String msg) {
        super(msg);
    }

    public SingularStudioException(Throwable cause) {
        super(cause);
    }

    public SingularStudioException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
