/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
