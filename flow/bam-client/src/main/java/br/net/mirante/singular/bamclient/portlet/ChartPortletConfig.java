package br.net.mirante.singular.bamclient.portlet;

import br.net.mirante.singular.bamclient.chart.SingularChart;

public abstract class ChartPortletConfig<T extends ChartPortletConfig<T>> extends PortletConfig<T> {

    private String restEndpointURL;
    private SingularChart chart;

    public ChartPortletConfig() {
    }

    public ChartPortletConfig(String restEndpointURL, SingularChart chart) {
        this.restEndpointURL = restEndpointURL;
        this.chart = chart;
    }

    public SingularChart getChart() {
        return chart;
    }

    public void setChart(SingularChart chart) {
        this.chart = chart;
    }

    public String getRestEndpointURL() {
        return restEndpointURL;
    }

    public void setRestEndpointURL(String restEndpointURL) {
        this.restEndpointURL = restEndpointURL;
    }
}
