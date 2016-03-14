/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;

public class SInstanceListElementRemovedEvent extends SInstanceStructureChangeEvent {

    private final SInstance removedInstance;
    private final int        index;

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
