/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.portlet.filter;

public enum AggregationPeriod {

    WEEKLY("Semanal"),
    MONTHLY("Mensal"),
    YEARLY("Anual"),
    BIMONTHLY("Bimestral");

    private String description;

    AggregationPeriod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
