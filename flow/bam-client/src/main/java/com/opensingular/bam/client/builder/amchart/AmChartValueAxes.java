/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.builder.amchart;

import com.opensingular.bam.client.builder.JSONObjectMappper;

public class AmChartValueAxes extends JSONObjectMappper<AmChartValueAxes> {

    public AmChartValueAxes gridColor(String value) {
        return put("gridColor", value);
    }

    public AmChartValueAxes gridAlpha(Number value) {
        return put("gridAlpha", value);
    }

    public AmChartValueAxes dashLength(Number value) {
        return put("dashLength", value);
    }

    @Override
    public AmChartValueAxes self() {
        return this;
    }

}
