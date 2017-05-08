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

package org.opensingular.flow.core.property;

import java.io.Serializable;

/**
 * Representa um par propriedade e seu valor.
 *
 * @author Daniel C. Bordin
 */
public class MetaDataValue implements Serializable {

    private final String name;
    private Serializable value;

    public Serializable getValue() {
        return value;
    }

    public MetaDataValue(MetaDataRef<?> propRef) {
        this.name = propRef.getName();
    }

    final void setValue(Serializable value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }
}
