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

import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Representa um mapa de propriedade com controle e conversão de acordo o tipo
 * de cada propriedade.
 *
 * @author Daniel C. Bordin
 */
public class MetaDataMap implements Iterable<MetaDataEntry>, Serializable {

    private LinkedHashMap<String, MetaDataEntry> metaDataKeyValue;

    public boolean isEmpty() {
        return metaDataKeyValue == null || metaDataKeyValue.isEmpty();
    }

    public Collection<MetaDataEntry> asCollection() {
        return (metaDataKeyValue == null) ? Collections.emptyList() : metaDataKeyValue.values();
    }

    public Stream<MetaDataEntry> stream() {
        return metaDataKeyValue == null ? Stream.empty() : asCollection().stream();
    }

    @Override
    public Iterator<MetaDataEntry> iterator() {
        return (metaDataKeyValue == null) ? Collections.emptyIterator() : metaDataKeyValue.values().iterator();
    }

    public <T extends Serializable> void set(MetaDataKey<T> key, T value) {
        if (value == null) {
            remove(key);
        } else {
            MetaDataEntry p = getOrCreate(key);
            p.setValue(value);
        }
    }

    private MetaDataEntry getOrCreate(MetaDataKey<?> key) {
        MetaDataEntry p = null;
        if (metaDataKeyValue == null) {
            metaDataKeyValue = new LinkedHashMap<>();
        } else {
            p = metaDataKeyValue.get(key.getName());
        }
        if (p == null) {
            p = new MetaDataEntry(key);
            metaDataKeyValue.put(key.getName(), p);
        }
        return p;
    }

    public void remove(MetaDataKey<?> key) {
        if (metaDataKeyValue != null) {
            metaDataKeyValue.remove(key.getName());
        }
    }

    @Nonnull
    public <T extends Serializable> T get(MetaDataKey<T> key) {
        if (key.getDefaultValue() == null) {
            throw new SingularException(MetaDataKey.class.getSimpleName() + " '" + key.getName() +
                    "' don't have a default value configured. Use method getOpt() or configure a default value for " +
                    "the key");
        }
        T value = getInternal(key);
        return value != null ? value : key.getDefaultValue();
    }

    /** Returns the value associated to the meta data key if available. */
    @Nonnull
    public <T extends Serializable> Optional<T> getOpt(@Nonnull MetaDataKey<T> key) {
        T value = getInternal(key);
        return value != null ? Optional.of(value) : Optional.ofNullable(key.getDefaultValue());
    }

    @Nullable
    private <T extends Serializable> T getInternal(@Nonnull MetaDataKey<T> key) {
        if (metaDataKeyValue != null) {
            MetaDataEntry p = metaDataKeyValue.get(key.getName());
            if (p != null) {
                return key.getValueClass().cast(p.getValue());
            }
        }
        return null;
    }
}
