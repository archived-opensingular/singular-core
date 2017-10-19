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

package org.opensingular.flow.core.variable.type;

import org.opensingular.flow.core.variable.VarDefinition;
import org.opensingular.flow.core.variable.VarInstance;

import javax.annotation.Nonnull;

public class VarTypeInteger extends VarTypeBase<Integer> {

    public VarTypeInteger() {
        super(Integer.class);
    }

    @Override
    public String toDisplayString(Object value, VarDefinition varDefinition) {
        return Integer.toString(convert(value));
    }

    @Override
    public String toPersistenceString(VarInstance varInstance) {
        return Integer.toString(convert(varInstance.getValue()));
    }

    @Override
    public Integer fromPersistenceStringImpl(String persistenceValue) {
        return persistenceValue == null ? null : Integer.valueOf(persistenceValue);
    }

    @Override
    protected Integer convertNotDirectCompatible(@Nonnull Object original) {
        if (original instanceof Number) {
            return ((Number) original).intValue();
        }
        return null;
    }
}
