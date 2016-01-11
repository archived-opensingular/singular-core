package br.net.mirante.singular.bamclient.chart;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import br.net.mirante.singular.bamclient.portlet.PortletFilterContext;

public interface ChartDataProvider extends Serializable {

    List<Map<String, String>> loadData(PortletFilterContext filterContext);

}
