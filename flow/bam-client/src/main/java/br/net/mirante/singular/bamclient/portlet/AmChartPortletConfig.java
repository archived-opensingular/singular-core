package br.net.mirante.singular.bamclient.portlet;

public class AmChartPortletConfig extends ChartPortletConfig {

    private String chartDefinition;

    public void setDefinition(String chartDefinition) {
        this.chartDefinition = chartDefinition;
    }

    public String getChartDefinition() {
        return chartDefinition;
    }
}
