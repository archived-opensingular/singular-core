/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.bamclient.portlet;

import org.opensingular.singular.bamclient.chart.SingularChart;

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
