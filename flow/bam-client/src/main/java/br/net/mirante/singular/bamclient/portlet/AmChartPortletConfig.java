package br.net.mirante.singular.bamclient.portlet;

import br.net.mirante.singular.bamclient.chart.SingularChart;

public class AmChartPortletConfig extends ChartPortletConfig<AmChartPortletConfig> {

    private final SingularChart chart;

    public AmChartPortletConfig(String restEndpointURL, SingularChart chart) {
        super(restEndpointURL);
        this.chart = chart;
    }

    public SingularChart getChart() {
        return chart;
    }

    @Override
    public AmChartPortletConfig self() {
        return this;
    }
}
