package br.net.mirante.singular.bamclient.portlet;

import br.net.mirante.singular.bamclient.chart.SingularChart;

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
