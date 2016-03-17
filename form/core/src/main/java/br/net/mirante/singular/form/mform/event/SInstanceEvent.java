/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.SInstance;

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
