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

import org.opensingular.flow.core.property.MetaData;

import javax.annotation.Nullable;
import java.io.Serializable;

//TODO marcar a variável quando esta for utilizada. Essa interface deve obrigar a implementacao de um metodo para essa verificacao
public interface VarInstance extends Serializable {

    VarInstance setValue(Object valor);

    default void setValueFromPersistence(@Nullable String persistenceValue) {
        if (persistenceValue == null) {
            setValue(null);
        } else {
            setValue(getDefinition().fromPersistenceString(persistenceValue));
        }
    }

    VarDefinition getDefinition();

    Object getValue();

    String getStringDisplay();

    String getPersistentString();

    MetaData getMetaData();

    @SuppressWarnings("unchecked")
    default <T> T getValue(T defaultValue) {
        T v = (T) getValue();
        return (v == null) ? defaultValue : v;
    }

    default String getRef() {
        return getDefinition().getRef();
    }

    default String getName() {
        return getDefinition().getName();
    }

    default boolean isRequired() {
        return getDefinition().isRequired();
    }

    default VarType getType() {
        return getDefinition().getType();
    }

    void setChangeListner(VarInstanceMap<?,?> changeListener);

}
