/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform;

public class TypeBuilder {

    private final SType<?> targetType;

    private final Class<? extends SType<?>> targetTypeClass;

    private boolean load;

    public <X extends SType<?>> TypeBuilder(Class<X> targetTypeClass, X targetType) {
        this.targetTypeClass = targetTypeClass;
        this.targetType = targetType;
    }

    public <X extends SType<?>> TypeBuilder(Class<X> targetTypeClass) {
        this.targetTypeClass = targetTypeClass;
        this.targetType = MapByName.newInstance(targetTypeClass);
        this.load = true;
    }

    public SType<?> getType() {
        return targetType;
    }

    public Class<? extends SType<?>> getTypeClass() {
        return targetTypeClass;
    }

    public SType<?> configure() {
        if (load) {
            targetType.onLoadType(this);
        }
        return targetType;
    }

}
