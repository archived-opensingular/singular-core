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

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import org.opensingular.form.type.basic.AtrBasic;
import org.opensingular.form.type.basic.AtrBootstrap;
import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.provider.AtrProvider;
import org.opensingular.form.type.core.annotation.AtrAnnotation;

/**
 * Representa um entidade habilitada para ter atributos lidos ou alterados.
 * Tipicamente os atributos são de uma instância, tipo ou um proxy de atributos.
 *
 * @author Daniel C. Bordin
 */
public interface SAttributeEnabled {

    default <V> void setAttributeCalculation(AtrRef<?, ?, V> atr, SimpleValueCalculation<V> value) {
        getDictionary().loadPackage(atr.getPackageClass());
        setAttributeCalculation(atr.getNameFull(), null, value);
    }

    <V> void setAttributeCalculation(String attributeFullName, String subPath, SimpleValueCalculation<V> value);

    default <V> void setAttributeValue(AtrRef<?, ?, V> atr, V value) {
        getDictionary().loadPackage(atr.getPackageClass());
        setAttributeValue(atr.getNameFull(), null, value);
    }

    default <V> void setAttributeValue(SType<?> defAttribute, Object value) {
        defAttribute.checkIfIsAttribute();
        setAttributeValue(defAttribute.getName(), null, value);
    }

    default void setAttributeValue(String attributeName, Object value) {
        setAttributeValue(attributeName, null, value);
    }

    void setAttributeValue(String attributeFullName, String subPath, Object value);


    /**
     * Lista todos os atributos com valor associado diretamente ao objeto atual.
     * @return Nunca null
     */
    public Collection<SInstance> getAttributes();

    /** Retorna a instancia do atributo se houver uma associada diretamente ao objeto atual. */
    public Optional<SInstance> getAttribute(String fullName);

    <V> V getAttributeValue(String attributeFullName, Class<V> resultClass);

    default <T> T getAttributeValue(AtrRef<?, ?, ?> atr, Class<T> resultClass) {
        getDictionary().loadPackage(atr.getPackageClass());
        return getAttributeValue(atr.getNameFull(), resultClass);
    }

    default <V> V getAttributeValue(AtrRef<?, ?, V> atr) {
        getDictionary().loadPackage(atr.getPackageClass());
        return getAttributeValue(atr.getNameFull(), atr.getValueClass());
    }

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
     * Retorna o leitor de atributos de anotação para o tipo ou instância atual.
     */
    public default AtrAnnotation asAtrAnnotation() {
        return as(AtrAnnotation::new);
    }

    default AtrProvider asAtrProvider() {
        return as(AtrProvider::new);
    }
}
