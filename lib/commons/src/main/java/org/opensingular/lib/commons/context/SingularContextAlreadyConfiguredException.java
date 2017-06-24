package org.opensingular.lib.commons.context;

import org.opensingular.lib.commons.base.SingularException;

public class SingularContextAlreadyConfiguredException extends SingularException {

    public SingularContextAlreadyConfiguredException() {
        super("Singular context is alread configured, try calling reset() before attempting do reconfigure.");
    }
}
