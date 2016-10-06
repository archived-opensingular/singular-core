/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.model;

import org.apache.wicket.model.IModel;

public interface IReadOnlyModel<T> extends IModel<T> {

    default void setObject(T object) {
        throw new UnsupportedOperationException("Model " + getClass() +
            " does not support setObject(Object)");
    }
    
    default void detach() {
    }
    
    public static <U> IReadOnlyModel<U> of(IReadOnlyModel<U> model) {
        return model;
    }
}
