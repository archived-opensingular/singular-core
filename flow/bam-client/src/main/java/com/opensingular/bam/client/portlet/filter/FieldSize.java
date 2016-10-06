/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.portlet.filter;

public enum FieldSize {

    SMALL(3),
    MEDIUM(6),
    LARGE(12);

    private int size;

    FieldSize(int size) {
        this.size = size;
    }

    public int getBootstrapSize() {
        return size;
    }
}
