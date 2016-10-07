/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.core;

public class SILong extends SINumber<Long> implements SIComparable<Long> {

    public SILong() {
    }

    public Long getLong() {
        return (Long) getValue();
    }
}
