/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core;

public class SIInteger extends SIComparable<Integer> {

    public SIInteger() {
    }

    public Integer getInteger() {
        return (Integer) getValue();
    }
}
