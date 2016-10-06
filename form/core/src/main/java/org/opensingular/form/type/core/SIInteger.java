/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.core;

public class SIInteger extends SINumber<Integer> implements SIComparable<Integer> {

    public SIInteger() {
    }

    public Integer getInteger() {
        return (Integer) getValue();
    }
}
