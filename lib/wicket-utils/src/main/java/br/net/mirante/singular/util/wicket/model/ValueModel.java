/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.model;

import java.io.Serializable;
import java.util.Objects;

import org.apache.wicket.model.Model;

import br.net.mirante.singular.commons.lambda.IFunction;

public final class ValueModel<T extends Serializable> extends Model<T> {
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