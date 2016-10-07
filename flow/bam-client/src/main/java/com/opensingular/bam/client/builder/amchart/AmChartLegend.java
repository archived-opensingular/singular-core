/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.builder.amchart;


import com.opensingular.bam.client.builder.JSONObjectMappper;

public class AmChartLegend extends JSONObjectMappper<AmChartLegend> {

    public AmChartLegend useGraphSettings(boolean value) {
        return put("useGraphSettings", value);
    }

    @Override
    public AmChartLegend self() {
        return this;
    }
}
