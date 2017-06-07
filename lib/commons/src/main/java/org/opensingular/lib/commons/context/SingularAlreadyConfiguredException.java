package org.opensingular.lib.commons.context;

import org.opensingular.lib.commons.base.SingularException;

public class SingularAlreadyConfiguredException extends SingularException {

    public SingularAlreadyConfiguredException() {
        super("Singular context is alread configured, try calling reset() before attempting do reconfigure.");
    }
}
