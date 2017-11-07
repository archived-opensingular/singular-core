/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.lib.commons.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * It's a immutable representation of a property key and it's associated value.
 *
 * @author Daniel C. Bordin
 * @since 2017-11-02
 */
public final class PropertyEntry implements Comparable<PropertyEntry>{

    @Nonnull
    private final String key;
    @Nullable
    private final String value;
    @Nonnull
    private final PropertySource<?> source;

    public PropertyEntry(@Nonnull String key, @Nullable String value, @Nonnull PropertySource<?> source) {
        this.key = key;
        this.value = value;
        this.source = source;
    }

    @Nonnull
    public String getKey() {
        return key;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    @Nonnull
    public PropertySource<?> getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(key, ((PropertyEntry) o).key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public int compareTo(PropertyEntry o) {
        return key.compareTo(o.getKey());
    }
}
