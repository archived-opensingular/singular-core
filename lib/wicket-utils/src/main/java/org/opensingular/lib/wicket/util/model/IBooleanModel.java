/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.model;

public interface IBooleanModel extends IReadOnlyModel<Boolean> {

    default IBooleanModel not() {
        return () -> !Boolean.TRUE.equals(getObject());
    }
}
