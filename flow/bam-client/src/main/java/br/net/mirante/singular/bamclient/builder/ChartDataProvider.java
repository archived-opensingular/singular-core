package br.net.mirante.singular.bamclient.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.net.mirante.singular.bamclient.portlet.PortletFilterContext;

public class ChartDataProvider implements Serializable {

    private final List<Map<String, String>> dataList = new ArrayList<>();

    public ChartDataProvider addData(Map<String, String> data) {
        dataList.add(data);
        return this;
    }

    public ChartDataProvider addAll(List<Map<String, String>> data) {
        dataList.addAll(data);
        return this;
    }

    public List<Map<String, String>> getData(PortletFilterContext filterContext) {
        return dataList;
    }
}
