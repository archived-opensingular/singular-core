/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;

import java.util.LinkedHashMap;
import java.util.Map;

public class FromPojo<T> {

    protected STypeComposite<? extends SIComposite> target;
    private T pojo;
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
        return (R)instancia;
    }

    @FunctionalInterface
    public static interface FromPojoFiedlBuilder<T> {
        Object value(T pojo);
    }
}
