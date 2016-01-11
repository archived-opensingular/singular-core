package br.net.mirante.singular.bamclient.chart;

import java.util.List;
import java.util.Map;

import br.net.mirante.singular.bamclient.portlet.PortletFilterContext;


public class RestChartDataProvider implements ChartDataProvider {

    private final String url;

    public RestChartDataProvider(String url) {
        this.url = url;
    }

    @Override
    public List<Map<String, String>> loadData(PortletFilterContext filterContext) {
        //TODO Implementando Cliente Rest
        return null;
    }

}
