/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.core;

public class SIInteger extends SINumber<Integer> implements SIComparable<Integer> {

    public SIInteger() {
    }

    public Integer getInteger() {
        return (Integer) getValue();
    }
}
