package br.net.mirante.singular.bamclient.portlet;

public abstract class ChartPortletConfig<T extends ChartPortletConfig> extends PortletConfig<T> {

    private String restEndpointURL;

    public ChartPortletConfig(String restEndpointURL) {
        this.restEndpointURL = restEndpointURL;
    }

    public String getRestEndpointURL() {
        return restEndpointURL;
    }

    public void setRestEndpointURL(String restEndpointURL) {
        this.restEndpointURL = restEndpointURL;
    }
}
