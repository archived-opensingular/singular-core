/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.chart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.net.mirante.singular.bamclient.builder.amchart.AmChartGraph;
import br.net.mirante.singular.bamclient.builder.amchart.AmChartValueField;

public class ColumnSerialChart extends AbstractSerialChart {

    public ColumnSerialChart() {
    }

    public ColumnSerialChart(String category, AmChartValueField... values) {
        super(category, values);
    }

    public ColumnSerialChart(String category, List<AmChartValueField> values) {
        super(category, values);
    }

    @Override
    protected Collection<AmChartGraph> getGraphs() {
        final List<AmChartGraph> graphs = new ArrayList<>();
        values.forEach(v -> {
            graphs.add(new AmChartGraph()
                    .balloonText(String.format("[[category]]: <b>[[value]] %s </b>", v.getSuffix()))
                    .type("column")
                    .valueField(v.getPropertyName())
                    .title(v.getTitle())
                    .lineAlpha(0.2)
                    .fillAlphas(0.8));
        });
        return graphs;
    }
}
