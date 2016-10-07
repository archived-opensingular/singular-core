/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.util.transformer;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;

import java.util.LinkedHashMap;
import java.util.Map;

public class FromPojo<T> {

    protected STypeComposite<? extends SIComposite> target;
    private   T                                     pojo;
    protected Map<SType, FromPojoFiedlBuilder> mappings = new LinkedHashMap<>();

    public FromPojo(STypeComposite<? extends SIComposite> target, T pojo) {
        this.target = target;
        this.pojo = pojo;
    }

    public FromPojo(STypeComposite<? extends SIComposite> target) {
        this.target = target;
    }

    public <K extends SType<?>> FromPojo<T> map(K type, FromPojoFiedlBuilder<T> mapper) {
        mappings.put(type, mapper);
        return this;
    }

    public <K extends SType<?>> FromPojo<T> map(K type, Object value) {
        mappings.put(type, p -> value);
        return this;
    }

    public <R extends SInstance> R build() {
        SIComposite instancia = target.newInstance();
        for (Map.Entry<SType, FromPojoFiedlBuilder> e : mappings.entrySet()) {
            instancia.setValue(e.getKey().getName(), e.getValue().value(pojo));
        }
        return (R) instancia;
    }

    @FunctionalInterface
    public static interface FromPojoFiedlBuilder<T> {
        Object value(T pojo);
    }
}
