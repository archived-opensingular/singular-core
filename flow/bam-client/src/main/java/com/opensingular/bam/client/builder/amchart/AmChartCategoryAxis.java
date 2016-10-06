/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.builder.amchart;

import com.opensingular.bam.client.builder.JSONObjectMappper;

public class AmChartCategoryAxis extends JSONObjectMappper<AmChartCategoryAxis> {

    public AmChartCategoryAxis gridPosition(String value) {
        return put("gridPosition", value);
    }

    public AmChartCategoryAxis gridAlpha(Number value) {
        return put("gridAlpha", value);
    }

    public AmChartCategoryAxis tickPosition(String value) {
        return put("tickPosition", value);
    }

    public AmChartCategoryAxis tickLength(Number value) {
        return put("tickLength", value);
    }

    public AmChartCategoryAxis autoWrap(boolean value) {
        return put("autoWrap", value);
    }

    @Override
    public AmChartCategoryAxis self() {
        return this;
    }
}
