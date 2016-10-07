/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.property;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

/**
 * Representa um mapa de propriedade com controle e conversão de acordo o tipo
 * de cada propriedade.
 *
 * @author Daniel C. Bordin
 */
public class MetaData implements Iterable<MetaDataValue>, Serializable {

    private LinkedHashMap<String, MetaDataValue> metaDataKeyValue;

    public boolean isEmpty() {
        return metaDataKeyValue == null || metaDataKeyValue.isEmpty();
    }

    public Collection<MetaDataValue> asCollection() {
        return (metaDataKeyValue == null) ? Collections.emptyList() : metaDataKeyValue.values();
    }

    public Stream<MetaDataValue> stream() {
        return asCollection().stream();
    }

    @Override
    public Iterator<MetaDataValue> iterator() {
        return (metaDataKeyValue == null) ? Collections.emptyIterator() : metaDataKeyValue.values().iterator();
    }

    public <T> void set(MetaDataRef<T> propRef, T value) {
        if (value == null) {
            remove(propRef);
        } else {
            MetaDataValue p = getOrCreate(propRef);
            p.setValue(value);
        }
    }

    private MetaDataValue getOrCreate(MetaDataRef<?> propRef) {
        MetaDataValue p = null;
        if (metaDataKeyValue == null) {
            metaDataKeyValue = new LinkedHashMap<>();
        } else {
            p = metaDataKeyValue.get(propRef.getName());
        }
        if (p == null) {
            p = new MetaDataValue(propRef);
            metaDataKeyValue.put(propRef.getName(), p);
        }
        return p;
    }

    public void remove(MetaDataRef<?> propRef) {
        if (metaDataKeyValue != null) {
            metaDataKeyValue.remove(propRef.getName());
        }
    }

    public <T> T get(MetaDataRef<T> propRef) {
        if (metaDataKeyValue != null) {
            MetaDataValue p = metaDataKeyValue.get(propRef.getName());
            if (p != null) {
                return propRef.getValueClass().cast(p.getValue());
            }
        }
        return null;
    }
}
