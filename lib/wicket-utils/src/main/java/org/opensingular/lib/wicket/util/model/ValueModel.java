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

package org.opensingular.lib.wicket.util.model;

import java.io.Serializable;
import java.util.Objects;

import org.apache.wicket.model.Model;

import org.opensingular.lib.commons.lambda.IFunction;

public final class ValueModel<T extends Serializable>
        extends Model<T>
        implements IMappingModel<T> {
    
    private final IFunction<T, Object> equalsHashArgsFunc;
    
    public ValueModel(T object, IFunction<T, Object> equalsHashArgsFunc) {
        super(object);
        this.equalsHashArgsFunc = equalsHashArgsFunc;
    }
    @Override
    public int hashCode() {
        return Objects.hash(equalsHashArgsFunc.apply(this.getObject()));
    }
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueModel<?>))
            return false;
        return Objects.deepEquals(
            equalsHashArgsFunc.apply(this.getObject()),
            equalsHashArgsFunc.apply(((ValueModel<T>) obj).getObject()));
    }
}