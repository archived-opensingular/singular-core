package org.opensingular.lib.commons.context;

import org.opensingular.lib.commons.base.SingularException;

public class SingularSingletonNotFoundException extends SingularException {

    public SingularSingletonNotFoundException(String key) {
        super(String.format("We could not find the singleton you asked for (key: %s), sorry. Check if the singleton is registered.", key));
    }
}
