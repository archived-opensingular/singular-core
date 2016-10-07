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

public class SICode<T> extends SInstance {

    private T code;

    public SICode() {}

    @Override
    public T getValue() {
        return code;
    }

    @Override
    public void clearInstance() {
       setValue(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public STypeCode<SICode<T>, T> getType() {
        return (STypeCode<SICode<T>, T>) super.getType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Object valor) {
        this.code = (T) valor;
    }

    @Override
    public boolean isEmptyOfData() {
        return code != null;
    }

    @Override
    public String toStringDisplayDefault() {
        return getType().getNameSimple();
    }
}
