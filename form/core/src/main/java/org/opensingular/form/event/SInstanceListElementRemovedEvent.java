/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.event;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;

public class SInstanceListElementRemovedEvent extends SInstanceStructureChangeEvent {

    private final SInstance removedInstance;
    private final int       index;

    public SInstanceListElementRemovedEvent(SIList<? extends SInstance> source, SInstance removedInstance, int index) {
        super(source);
        this.removedInstance = removedInstance;
        this.index = index;
    }

    public SInstance getRemovedInstance() {
        return removedInstance;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + getIndex() + "] -= " + getRemovedInstance();
    }
}
