/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.event;

import br.net.mirante.singular.form.SInstance;

public class SInstanceValueChangeEvent extends SInstanceEvent {

    private final Object oldValue;
    private final Object newValue;

    public SInstanceValueChangeEvent(SInstance instance, Object oldValue, Object newValue) {
        super(instance);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }
    public Object getNewValue() {
        return newValue;
    }

    @Override
    public String toString() {
        return super.toString() + " = " + getOldValue() + " => " + getNewValue();
    }
}
