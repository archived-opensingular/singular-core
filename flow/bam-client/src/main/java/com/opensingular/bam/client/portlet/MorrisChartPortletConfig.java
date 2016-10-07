/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.portlet;

import com.opensingular.bam.client.chart.SingularChart;

public class MorrisChartPortletConfig extends ChartPortletConfig<MorrisChartPortletConfig> {

    public MorrisChartPortletConfig() {
    }

    public MorrisChartPortletConfig(DataEndpoint endpoint, SingularChart chart) {
        super(endpoint, chart);
    }

    @Override
    public MorrisChartPortletConfig self() {
        return this;
    }
}
