package br.net.mirante.singular.bamclient.portlet;

import br.net.mirante.singular.bamclient.chart.SingularChart;

public class MorrisChartPortletConfig extends ChartPortletConfig<MorrisChartPortletConfig> {

    public MorrisChartPortletConfig() {
    }

    public MorrisChartPortletConfig(String restEndpointURL, SingularChart chart) {
        super(restEndpointURL, chart);
    }

    @Override
    public MorrisChartPortletConfig self() {
        return this;
    }
}
