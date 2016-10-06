package com.opensingular.studio.core;

import org.opensingular.lib.commons.base.SingularException;

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
