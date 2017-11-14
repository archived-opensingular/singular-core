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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a property entry with values of a specific class.
 * @author Daniel C. Bordin
 */
public class MetaDataKey<K extends Serializable> implements Serializable {

    private final String name;
    private final Class<K> valueClass;
    private final K defaultValue;

    private MetaDataKey(@Nonnull String name, @Nonnull Class<K> valueClass, @Nullable K defaultValue) {
        this.name = Objects.requireNonNull(name);
        this.valueClass = Objects.requireNonNull(valueClass);
        this.defaultValue = defaultValue;
    }

    @Nonnull
    public static <T extends Serializable> MetaDataKey<T> of(@Nonnull String name, @Nonnull Class<T> valueClass) {
        return new MetaDataKey<T>(name, valueClass, null);
    }

    @Nonnull
    public static <T extends Serializable> MetaDataKey<T> of(@Nonnull String name, @Nonnull Class<T> valueClass, @Nullable T defaultValue) {
        return new MetaDataKey<T>(name, valueClass, defaultValue);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public K getDefaultValue() {
        return defaultValue;
    }

    @Nonnull
    public Class<K> getValueClass() {
        return valueClass;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return name.equals(((MetaDataKey<?>) obj).name);
    }
}
