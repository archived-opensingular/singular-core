package br.net.mirante.singular.bamclient.chart;

import br.net.mirante.singular.bamclient.builder.amchart.AmPieChartBuilder;

public class DonutPieChart extends PieChart {

    public DonutPieChart() {
    }

    public DonutPieChart(String valueProperty, String categoryProperty) {
        super(valueProperty, categoryProperty);
    }

    @Override
    protected void addOthersConfigs(AmPieChartBuilder chartBuilder) {
        chartBuilder.innerRadius(40);
    }
}
