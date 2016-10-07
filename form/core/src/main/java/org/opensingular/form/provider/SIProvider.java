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

package org.opensingular.form.provider;

import org.opensingular.form.SInstance;

import java.io.Serializable;

public class SIProvider<P extends Provider<T, SInstance>, T extends Serializable> extends SInstance {

    private P provider;

    @Override
    public Object getValue() {
        return provider;
    }

    @Override
    public void clearInstance() {
        provider = null;
    }

    @Override
    public boolean isEmptyOfData() {
        return provider != null;
    }

    @Override
    public void setValue(Object value) {
        this.provider = (P) value;
    }
}
