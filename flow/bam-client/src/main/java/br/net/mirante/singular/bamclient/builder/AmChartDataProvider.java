package br.net.mirante.singular.bamclient.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AmChartDataProvider {

    private final List<Map<String, String>> dataList = new ArrayList<>();

    public AmChartDataProvider addData(Map<String, String> data) {
        dataList.add(data);
        return this;
    }

    public AmChartDataProvider addAll(List<Map<String, String>> data) {
        dataList.addAll(data);
        return this;
    }

    public List<Map<String, String>> getDataList() {
        return dataList;
    }
}
