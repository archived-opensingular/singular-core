/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.server.commons.exception;

import org.opensingular.lib.commons.base.SingularException;

/**
 * Exceção para quando uma petição for modificada
 * de forma concorrente
 */
public class PetitionConcurrentModificationException extends SingularException {

    public PetitionConcurrentModificationException(String msg) {
        super(msg);
    }

    public PetitionConcurrentModificationException(Throwable cause) {
        super(cause);
    }

    public PetitionConcurrentModificationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
