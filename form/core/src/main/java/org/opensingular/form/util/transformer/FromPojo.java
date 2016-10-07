/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
