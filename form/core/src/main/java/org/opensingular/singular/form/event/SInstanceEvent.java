/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.event;

import org.opensingular.singular.form.SInstance;

public abstract class SInstanceEvent {

    private final SInstance source;

    protected SInstanceEvent(SInstance source) {
        this.source = source;
    }

    public SInstance getSource() {
        return source;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getSource();
    }
}
