package br.net.mirante.singular.bamclient.portlet;

import br.net.mirante.singular.bamclient.chart.SingularChart;

public class AmChartPortletConfig extends ChartPortletConfig<AmChartPortletConfig> {

    private final SingularChart chart;

    public AmChartPortletConfig(SingularChart chart) {
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
