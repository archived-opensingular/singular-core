/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.model;

public interface IBooleanModel extends IReadOnlyModel<Boolean> {

    default IBooleanModel not() {
        return () -> !Boolean.TRUE.equals(getObject());
    }
}
