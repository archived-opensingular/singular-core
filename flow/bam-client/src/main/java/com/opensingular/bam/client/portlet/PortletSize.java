/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.portlet;

public enum PortletSize {

    SMALL(3),
    MEDIUM(6),
    LARGE(12);

    private int size;

    PortletSize(int size) {
        this.size = size;
    }

    public String getBootstrapSize() {
        String template = "col-lg-%s col-md-%s";
        return String.format(template, size, size * 2 > 12 ? 12 : size * 2);
    }

    public int getSize() {
        return size;
    }
}
