/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.chart;

import br.net.mirante.singular.bamclient.builder.SingularChartBuilder;
import br.net.mirante.singular.bamclient.builder.amchart.AmPieChartBuilder;

public class PieChart implements SingularChart {

    private String valueProperty;
    private String categoryProperty;

    public PieChart() {
    }

    public PieChart(String categoryProperty, String valueProperty) {
        this.valueProperty = valueProperty;
        this.categoryProperty = categoryProperty;
    }

    @Override
    public String getDefinition() {
        final AmPieChartBuilder chartBuilder = new SingularChartBuilder()
                .newPieChart()
                .startDuration(0.5)
                .startEffect("easeOutSine")
                .angle(12)
                .balloonText("[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>")
                .depth3D(15)
                .labelRadius(30)
                .minRadius(50)
                .titleField(categoryProperty)
                .valueField(valueProperty);

        addOthersConfigs(chartBuilder);

        return chartBuilder.finish();
    }


    protected void addOthersConfigs(AmPieChartBuilder chartBuilder) {
    }

    public String getValueProperty() {
        return valueProperty;
    }

    public void setValueProperty(String valueProperty) {
        this.valueProperty = valueProperty;
    }

    public String getCategoryProperty() {
        return categoryProperty;
    }

    public void setCategoryProperty(String categoryProperty) {
        this.categoryProperty = categoryProperty;
    }
}
