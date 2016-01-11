package br.net.mirante.singular.bamclient.chart;

import java.util.List;
import java.util.Map;

import br.net.mirante.singular.bamclient.portlet.PortletFilterContext;

public abstract class StalessChartDataProvider implements ChartDataProvider {

    @Override
    public abstract List<Map<String, String>> loadData(PortletFilterContext filterContext);

}
