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
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensingular.lib.commons.base.SingularException;

/**
 * Indicates the capability of a class to have meta data information through a dynamic map of key and value.
 * @see MetaDataMap
 *
 * @author Daniel C. Bordin
 * @since 2017-05-04
 */
public interface MetaDataEnabled {

    @Nonnull
    public MetaDataMap getMetaData();

    @Nonnull
    public Optional<MetaDataMap> getMetaDataOpt();

    /**
     * Set a value associeted to the key. If the value is null, then reverted the value to default value of the key
     * ({@link MetaDataKey#getDefaultValue()}).
     */
    default <T extends Serializable> void setMetaDataValue(@Nonnull MetaDataKey<T> key, @Nullable T value) {
        getMetaData().set(key, value);
    }

    /**
     * Returns the value associated to the meta data key or the default value direct associated to the key.
     * <p>Throws a exception if the key don't have a default value associated to it. In this case, should be used
     * {@link #getMetaDataValueOpt(MetaDataKey)}</p>
     */
    @Nonnull
    default <T extends Serializable> T getMetaDataValue(@Nonnull MetaDataKey<T> key) {
        if (key.getDefaultValue() == null) {
            throw new SingularException(MetaDataKey.class.getSimpleName() + " '" + key.getName() +
                    "' don't have a default value configured. Use method getMetaDataValueOpt() or configure a default" +
                    " " + "value for the key");
        }
        return getMetaDataValueOpt(key).orElse(key.getDefaultValue());
    }

    /** Returns the value associated to the meta data key if available. */
    @Nonnull
    default <T extends Serializable> Optional<T> getMetaDataValueOpt(@Nonnull MetaDataKey<T> key) {
        Optional<MetaDataMap> map = getMetaDataOpt();
        return map.isPresent() ? map.get().getOpt(key) : Optional.ofNullable(key.getDefaultValue());
    }
}
