package br.net.mirante.singular.bamclient.builder;

import java.util.HashMap;
import java.util.Map;

public class AmChartGraph {

    private final Map<String, String> properties = new HashMap<>();

    public AmChartGraph setFillAlphas(String fillAlphas) {
        properties.put("fillAlphas", fillAlphas);
        return this;
    }

    public AmChartGraph setLineAlpha(String lineAlpha) {
        properties.put("lineAlpha", lineAlpha);
        return this;
    }

    public AmChartGraph setType(String type) {
        properties.put("type", type);
        return this;
    }

    public AmChartGraph setValueField(String valueField) {
        properties.put("valueField", valueField);
        return this;
    }

    public AmChartGraph setBalloonText(String balloonText) {
        properties.put("balloonText", balloonText);
        return this;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
