/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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