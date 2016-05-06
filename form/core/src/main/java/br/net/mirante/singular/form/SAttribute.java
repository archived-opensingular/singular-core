/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

public class SAttribute extends SType<SInstance> {

    private final boolean selfReference;

    private final SType<?> owner;

    SAttribute(String name, SType<? extends SInstance> type) {
        this(name, type, null, false);
    }

    SAttribute(String name, SType<? extends SInstance> type, SType<?> tipoDono, boolean selfReference) {
        super(name, (SType<SInstance>) type, null);
        this.owner = tipoDono;
        this.selfReference = selfReference;
    }

    @Override
    public boolean isSelfReference() {
        return selfReference;
    }

    final SInstance newInstanceFor(SType<?> owner) {
        SInstance instance;
        if (selfReference) {
            instance = owner.newInstance(getDictionary().getInternalDicionaryDocument());
        } else {
            instance = super.newInstance(getDictionary().getInternalDicionaryDocument());
        }
        instance.setAsAttribute(null);
        return instance;
    }

    public SType<?> getOwnerType() {
        return owner;
    }
}
