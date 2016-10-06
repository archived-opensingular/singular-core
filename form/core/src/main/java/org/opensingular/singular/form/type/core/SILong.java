/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.type.core;

public class SILong extends SINumber<Long> implements SIComparable<Long> {

    public SILong() {
    }

    public Long getLong() {
        return (Long) getValue();
    }
}
