/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.core;

import org.opensingular.form.SISimple;

public class SIString extends SISimple<String> implements SIComparable<String> {

    public SIString() {
    }

    @Override
    public STypeString getType() {
        return (STypeString) super.getType();
    }
}
