/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form;

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
