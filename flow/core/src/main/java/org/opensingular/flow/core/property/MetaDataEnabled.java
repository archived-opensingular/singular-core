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

package org.opensingular.flow.core.property;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Optional;

/**
 * Indica que o objeto é capaz de receber informações de meta dado sobre ele.
 *
 * @author Daniel C. Bordin on 04/05/2017.
 */
public interface MetaDataEnabled {

    @Nonnull
    public MetaData getMetaData();

    @Nonnull
    public Optional<MetaData> getMetaDataOpt();

    /**
     * <p>
     * Configura o valor do metadado especificado.
     * </p>
     *
     * @param <T>
     *            o tipo do metadado.
     * @param propRef
     *            o metadado especificado.
     * @param value
     *            o valor do metadado a ser configurado.
     * @return esta definição de processo já com o metadado definido.
     */
    default <T extends Serializable> void setMetaDataValue(@Nonnull MetaDataRef<T> propRef, T value) {
        getMetaData().set(propRef, value);
    }

    /**
     * <p>
     * Retorna o valor do metadado especificado.
     * </p>
     *
     * @param <T>
     *            o tipo do metadado.
     * @param propRef
     *            o metadado especificado.
     * @param defaultValue
     *            o valor padrão do metadado.
     * @return o valor do metadado especificado; ou o valor padrão caso não
     *         encontre o metadado especificado.
     */
    @Nonnull
    default <T extends Serializable> T getMetaDataValue(@Nonnull MetaDataRef<T> propRef, @Nonnull T defaultValue) {
        return MoreObjects.firstNonNull(getMetaDataValue(propRef), defaultValue);
    }

    /**
     * <p>
     * Retorna o valor do metadado especificado.
     * </p>
     *
     * @param <T>
     *            o tipo do metadado.
     * @param propRef
     *            o metadado especificado.
     * @return o valor do metadado especificado; ou {@code null} caso não
     *         encontre o metadado especificado.
     */
    @Nullable
    default  <T extends Serializable> T getMetaDataValue(@Nonnull MetaDataRef<T> propRef) {
        return getMetaDataOpt().map(m -> m.get(propRef)).orElse(null);
    }
}
