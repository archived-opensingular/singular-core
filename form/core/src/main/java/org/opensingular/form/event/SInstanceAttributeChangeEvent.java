/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.event;

import org.opensingular.form.SInstance;

public class SInstanceAttributeChangeEvent extends SInstanceEvent {

    private final SInstance attributeInstance;
    private final Object    oldValue;
    private final Object    newValue;

    public SInstanceAttributeChangeEvent(SInstance instance, SInstance attributeInstance, Object oldValue, Object newValue) {
        super(instance);
        this.attributeInstance = attributeInstance;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public SInstance getAttributeInstance() {
        return attributeInstance;
    }

    public Object getOldValue() {
        return oldValue;
    }
    public Object getNewValue() {
        return newValue;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getAttributeInstance() + "]: " + getSource() + " = " + getOldValue() + " => " + getNewValue();
    }
}
