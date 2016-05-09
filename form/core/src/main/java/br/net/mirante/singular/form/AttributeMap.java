/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

import java.util.*;

class AttributeMap implements Iterable<SAttribute> {

    private Map<String, SAttribute> attributes;

    final void add(SAttribute atributo) {
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
        } else if (attributes.containsKey(atributo.getName())) {
            throw new RuntimeException("Já existe um atributo '" + atributo.getName() + "' definido");
        }
        attributes.put(atributo.getName(), atributo);
    }

    public SAttribute get(String name) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(name);
    }

    public Collection<SAttribute> getAttributes() {
        if (attributes == null) {
            return Collections.emptyList();
        }
        return attributes.values();
    }

    @Override
    public Iterator<SAttribute> iterator() {
        if (attributes == null) {
            return Collections.emptyListIterator();
        }
        return attributes.values().iterator();
    }

}
