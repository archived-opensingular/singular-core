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

package org.opensingular.flow.core.variable;

import java.io.Serializable;

import org.opensingular.flow.core.property.MetaData;

/**
 * @deprecated traduzir o nome dos métodos para ingles
 */
@Deprecated
//TODO marcar a variável quando esta for utilizada. Essa interface deve obrigar a implementacao de um metodo para essa verificacao
public interface VarInstance extends Serializable {

    VarInstance setValor(Object valor);

    VarDefinition getDefinicao();

    Object getValor();

    String getStringDisplay();

    String getStringPersistencia();

    MetaData getMetaData();

    @SuppressWarnings("unchecked")
    default <T> T getValor(T defaultValue) {
        T v = (T) getValor();
        return (v == null) ? defaultValue : v;
    }

    default String getRef() {
        return getDefinicao().getRef();
    }

    default String getNome() {
        return getDefinicao().getName();
    }

    default boolean isObrigatorio() {
        return getDefinicao().isRequired();
    }

    default VarType getTipo() {
        return getDefinicao().getType();
    }

    void setChangeListner(VarInstanceMap<?> changeListener);
}
