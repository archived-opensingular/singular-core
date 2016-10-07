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

import org.opensingular.flow.core.property.MetaDataRef;

import java.io.Serializable;

public interface VarDefinition extends Serializable{

    public String getRef();

    public String getName();

    public VarType getType();

    public void setRequired(boolean value);

    public VarDefinition required();

    public boolean isRequired();

    public VarDefinition copy();

    public default String toDisplayString(VarInstance varInstance) {
        return getType().toDisplayString(varInstance);
    }

    public default String toDisplayString(Object valor) {
        return getType().toDisplayString(valor, this);
    }

    public default String toPersistenceString(VarInstance varInstance) {
        return getType().toPersistenceString(varInstance);
    }

    public <T> VarDefinition setMetaDataValue(MetaDataRef<T> propRef, T value);

    public <T> T getMetaDataValue(MetaDataRef<T> propRef, T defaultValue);

    public <T> T getMetaDataValue(MetaDataRef<T> propRef);

}
