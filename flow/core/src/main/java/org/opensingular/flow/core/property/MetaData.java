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

    public <T extends Serializable> void set(MetaDataRef<T> propRef, T value) {
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

    public <T extends Serializable> T get(MetaDataRef<T> propRef) {
        if (metaDataKeyValue != null) {
            MetaDataValue p = metaDataKeyValue.get(propRef.getName());
            if (p != null) {
                return propRef.getValueClass().cast(p.getValue());
            }
        }
        return null;
    }
}
