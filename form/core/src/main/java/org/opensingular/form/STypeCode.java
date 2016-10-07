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

package org.opensingular.form;

import org.opensingular.form.type.core.SPackageCore;

@SInfoType(name = "STypeCode", spackage = SPackageCore.class)
public class STypeCode<I extends SICode<V>, V> extends SType<I> {

    private Class<V> codeClass;

    public STypeCode() {}

    public STypeCode(Class<I> instanceClass, Class<V> valueClass) {
        super(instanceClass);
        this.codeClass = valueClass;
    }

    public Class<V> getCodeClass() {
        return codeClass;
    }
    @SuppressWarnings("unchecked")
    @Override
    public <C> C convert(Object valor, Class<C> classeDestino) {
        return (C) valor;
    }
}
