package br.net.mirante.singular.bamclient.chart;

import br.net.mirante.singular.bamclient.builder.SingularChartBuilder;

public class AreaChart implements SingularChart {

    private String categoryProperty;
    private String[] valueProperties;
    private String[] labels;

    public AreaChart(String categoryProperty, String... valueProperties) {
        this.categoryProperty = categoryProperty;
        this.valueProperties = valueProperties;
    }

    public AreaChart labels(String... labels){
        this.labels = labels;
        return this;
    }

    @Override
    public String getDefinition() {
        return new SingularChartBuilder()
                .newAreaChart()
                //.padding(0)
                //.behaveLikeLine(false)
                //.idEnabled(false)
                //.gridLineColor(false)
                //.axes(false)
                .fillOpacity(1D)
                .lineColors("#399a8c", "#92e9dc")
                .xkey(categoryProperty)
                //.pointSize(0)
                //.lineWidth(0)
                .ykeys(valueProperties)
                .labels(labels)
                .hideHover("auto")
                .resize(true)
                .finish();
    }

}
