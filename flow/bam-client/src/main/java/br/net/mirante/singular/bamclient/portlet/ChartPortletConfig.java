package br.net.mirante.singular.bamclient.portlet;

import br.net.mirante.singular.bamclient.chart.SingularChart;

public abstract class ChartPortletConfig<T extends ChartPortletConfig> extends PortletConfig<T> {

    private String restEndpointURL;
    private SingularChart chart;

    public ChartPortletConfig() {
    }

    public ChartPortletConfig(String restEndpointURL, SingularChart chart) {
        this.restEndpointURL = restEndpointURL;
        this.chart = chart;
    }

    public ChartPortletConfig(String restEndpointURL, SingularChart chart,
                              BamDashboardView dashboardView) {
        this(restEndpointURL, chart);
        setTitle(dashboardView.getTitle());
        setSubtitle(dashboardView.getSubtitle());
        setPortletSize(dashboardView.getPortletSize());

    }

    public SingularChart getChart() {
        return chart;
    }

    public T setChart(SingularChart chart) {
        this.chart = chart;
        return self();
    }

    public String getRestEndpointURL() {
        return restEndpointURL;
    }

    public T setRestEndpointURL(String restEndpointURL) {
        this.restEndpointURL = restEndpointURL;
        return self();
    }
}
