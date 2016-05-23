/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

public class TypeBuilder {

    private final SType<?> targetType;

    private final Class<? extends SType<?>> targetTypeClass;

    <X extends SType<?>> TypeBuilder(Class<X> targetTypeClass) {
        this.targetTypeClass = targetTypeClass;
        this.targetType = MapByName.newInstance(targetTypeClass);
    }

    final SType<?> getType() {
        return targetType;
    }

    public Class<? extends SType<?>> getTypeClass() {
        return targetTypeClass;
    }
}
