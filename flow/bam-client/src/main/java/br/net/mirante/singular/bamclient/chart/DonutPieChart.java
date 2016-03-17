/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.chart;

import br.net.mirante.singular.bamclient.builder.amchart.AmPieChartBuilder;

public class DonutPieChart extends PieChart {

    public DonutPieChart() {
    }

    public DonutPieChart(String categoryProperty, String valueProperty) {
        super(categoryProperty, valueProperty);
    }

    @Override
    protected void addOthersConfigs(AmPieChartBuilder chartBuilder) {
        chartBuilder.innerRadius(40);
    }
}
