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

package org.opensingular.form;

import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.provider.AtrProvider;
import org.opensingular.form.type.basic.AtrBasic;
import org.opensingular.form.type.basic.AtrBootstrap;
import org.opensingular.form.type.basic.AtrPersistence;
import org.opensingular.form.type.core.annotation.AtrAnnotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

/**
 * Representa um entidade habilitada para ter atributos lidos ou alterados.
 * Tipicamente os atributos são de uma instância, tipo ou um proxy de atributos.
 *
 * @author Daniel C. Bordin
 */
public interface SAttributeEnabled {

    <V> void setAttributeCalculation(@Nonnull AtrRef<?, ?, V> atr, @Nullable SimpleValueCalculation<V> value);

    <V> void setAttributeCalculation(@Nonnull String attributeFullName, @Nullable String subPath,
            @Nullable SimpleValueCalculation<V> value);

    <V> void setAttributeValue(@Nonnull AtrRef<?, ?, V> atr, @Nullable V value);

    default void setAttributeValue(SType<?> defAttribute, Object value) {
        defAttribute.checkIfIsAttribute();
        setAttributeValue(defAttribute.getName(), null, value);
    }

    default void setAttributeValue(String attributeName, Object value) {
        setAttributeValue(attributeName, null, value);
    }

    void setAttributeValue(String attributeFullName, String subPath, Object value);


    /**
     * Lista todos os atributos com valor associado diretamente ao objeto atual.
     */
    @Nonnull
    public Collection<SInstance> getAttributes();

    /**
     * Retorna a instancia do atributo se houver uma associada diretamente ao objeto atual. Não procura o atributo na
     * hierarquia.
     */
    @Nonnull
    public Optional<SInstance> getAttributeDirectly(@Nonnull String fullName);

    @Nullable
    <V> V getAttributeValue(@Nonnull String attributeFullName, @Nullable Class<V> resultClass);

    @Nullable
    <T> T getAttributeValue(@Nonnull AtrRef<?, ?, ?> atr, @Nullable Class<T> resultClass);

    @Nullable
    <V> V getAttributeValue(@Nonnull AtrRef<?, ?, V> atr);

    boolean hasAttribute(@Nonnull AtrRef<?, ?, ?> atr);

    default Object getAttributeValue(String attributeFullName) {
        return getAttributeValue(attributeFullName, null);
    }

    SDictionary getDictionary();

    /**
     * Transforma o tipo ou instância atual de acordo com a função de
     * mapeamento.
     */
    public <TR> TR as(Function<SAttributeEnabled, TR> wrapper);

    /** Retorna o leitor de atributos básicos para o tipo ou instância atual. */
    public default AtrBasic asAtr() {
        return as(AtrBasic::new);
    }

    /**
     * Retorna o leitor de atributos de Bootstrap para o tipo ou instância
     * atual.
     */
    public default AtrBootstrap asAtrBootstrap() {
        return as(AtrBootstrap::new);
    }

    /**
     * Retorna o leitor de atributos relacionados a persistencia dos dados.
     * @return
     */
    public default AtrPersistence asAtrPersistence() {
        return as(AtrPersistence::new);
    }

    /**
     * Retorna o leitor de atributos de anotação para o tipo ou instância atual.
     */
    public default AtrAnnotation asAtrAnnotation() {
        return as(AtrAnnotation::new);
    }

    default AtrProvider asAtrProvider() {
        return as(AtrProvider::new);
    }
}
