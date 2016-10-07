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

import java.util.*;

class AttributeMap implements Iterable<SType<?>> {

    private Map<String, SType<?>> attributes;

    final void add(SType<?> atributo) {
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
        } else if (attributes.containsKey(atributo.getName())) {
            throw new SingularFormException("Já existe um atributo '" + atributo.getName() + "' definido");
        }
        attributes.put(atributo.getName(), atributo);
    }

    public SType<?> get(String name) {
        return (attributes == null)  ? null : attributes.get(name);
    }

    public Collection<SType<?>> getAttributes() {
        return (attributes == null)  ? Collections.emptyList() : attributes.values();
    }

    @Override
    public Iterator<SType<?>> iterator() {
        return (attributes == null)  ? Collections.emptyListIterator() : attributes.values().iterator();
    }
}
