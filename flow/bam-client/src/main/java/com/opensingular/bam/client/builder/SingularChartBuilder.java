/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.builder;


import com.opensingular.bam.client.builder.amchart.AmSerialChartBuilder;
import com.opensingular.bam.client.builder.morris.MorrisAreaChart;
import com.opensingular.bam.client.builder.amchart.AmPieChartBuilder;

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
