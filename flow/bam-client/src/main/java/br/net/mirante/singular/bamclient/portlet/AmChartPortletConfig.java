package br.net.mirante.singular.bamclient.portlet;

import br.net.mirante.singular.bamclient.chart.SingularChart;

public class AmChartPortletConfig extends ChartPortletConfig<AmChartPortletConfig> {

    public AmChartPortletConfig(String restEndpointURL, SingularChart chart) {
        super(restEndpointURL, chart);
    }

    @Override
    public AmChartPortletConfig self() {
        return this;
    }

}
