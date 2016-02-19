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
