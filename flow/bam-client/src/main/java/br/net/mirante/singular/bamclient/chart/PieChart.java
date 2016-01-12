package br.net.mirante.singular.bamclient.chart;

import br.net.mirante.singular.bamclient.builder.SingularChartBuilder;
import br.net.mirante.singular.bamclient.builder.amchart.AmPieChartBuilder;

public class PieChart implements SingularChart {


    final private String valueProperty;
    final private String categoryProperty;

    public PieChart(String valueProperty, String categoryProperty) {
        this.valueProperty = valueProperty;
        this.categoryProperty = categoryProperty;
    }

    @Override
    public String getDefinition() {
        final AmPieChartBuilder chartBuilder = new SingularChartBuilder()
                .newPieChart()
                .angle(12)
                .marginTop(-50)
                .balloonText("[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>")
                .depth3D(15)
                .labelRadius(50)
                .titleField(categoryProperty)
                .valueField(valueProperty);

        addOthersConfigs(chartBuilder);

        return chartBuilder.finish();
    }


    protected void addOthersConfigs(AmPieChartBuilder chartBuilder){}
}
