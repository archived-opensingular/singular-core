/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.portlet;

import br.net.mirante.singular.bamclient.chart.SingularChart;

public class AmChartPortletConfig extends ChartPortletConfig<AmChartPortletConfig> {

    public AmChartPortletConfig() {
    }

    public AmChartPortletConfig(DataEndpoint endpoint, SingularChart chart) {
        super(endpoint, chart);
    }

    @Override
    public AmChartPortletConfig self() {
        return this;
    }

}
