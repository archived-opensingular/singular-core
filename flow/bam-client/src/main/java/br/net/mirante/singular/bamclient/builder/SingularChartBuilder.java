/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.builder;


import br.net.mirante.singular.bamclient.builder.amchart.AmPieChartBuilder;
import br.net.mirante.singular.bamclient.builder.amchart.AmSerialChartBuilder;
import br.net.mirante.singular.bamclient.builder.morris.MorrisAreaChart;

public class SingularChartBuilder extends AbstractJSONBuilder<SingularChartBuilder> {

    public SingularChartBuilder() {
        super(new JSONBuilderContext());
    }

    public AmSerialChartBuilder newSerialChart() {
        return new AmSerialChartBuilder(context);
    }

    public AmPieChartBuilder newPieChart() {
        return new AmPieChartBuilder(context);
    }

    public MorrisAreaChart newAreaChart() {
        return new MorrisAreaChart(context);
    }

    @Override
    public SingularChartBuilder self() {
        return this;
    }
}
