package org.opensingular.lib.commons.context;

import org.opensingular.lib.commons.base.SingularException;

public class SingularSingletonNotFoundException extends SingularException {

    public SingularSingletonNotFoundException() {
        super("We could not find the singleton you asked for, sorry. Check if the singleton is registered.");
    }

    public SingularSingletonNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SingularSingletonNotFoundException(Throwable cause) {
        super(cause);
    }

    public SingularSingletonNotFoundException(String key) {
        super(String.format("We could not find the singleton you asked for (key: %s), sorry. Check if the singleton is registered.", key));
    }
}
