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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class MapAttributeDefinitionResolver {

    private Map<String, SInstance> attributes;

    private final SType<?> owner;

    MapAttributeDefinitionResolver(SType<?> owner) {
        this.owner = owner;
    }

    public void set(String attributePath, Object value) {
        getCreating(attributePath).setValue(value);
    }

    public SInstance getCreating(String attributePath) {
        SInstance entry = get(attributePath);
        if (entry != null) {
            return entry;
        }

        for (SType<?> current = owner; current != null; current = current.getSuperType()) {
            SType<?> attrType = current.getAttributeDefinedLocally(attributePath);
            if (attrType != null) {
                SInstance attrInstance = attrType.newAttributeInstanceFor(owner);
                if (attributes == null) {
                    attributes = new LinkedHashMap<>();
                }
                attributes.put(attributePath, attrInstance);
                return attrInstance;
            }
        }
        if(owner != null) {
            throw new SingularFormException(
                    "Não existe o atributo '" + attributePath + "' definido em '" + owner.getName()
                    + "' ou nos tipos extendidos");
        } else {
            throw new SingularFormException("Não existe o atributo '" + attributePath + "'");
        }
    }

    /**
     * Lista todos os atributos definidos.
     *
     * @return Nunca null
     */
    public final Collection<SInstance> getAttributes() {
        return (attributes == null) ? Collections.emptyList() : attributes.values();
    }

    public SInstance get(String fullPathName) {
        return (attributes == null) ? null : attributes.get(fullPathName);
    }
}
