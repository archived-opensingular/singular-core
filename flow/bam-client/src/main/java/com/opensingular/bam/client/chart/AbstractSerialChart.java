/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.chart;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.opensingular.bam.client.builder.SingularChartBuilder;
import com.opensingular.bam.client.builder.amchart.AmChartCategoryAxis;
import com.opensingular.bam.client.builder.amchart.AmChartCursor;
import com.opensingular.bam.client.builder.amchart.AmChartGraph;
import com.opensingular.bam.client.builder.amchart.AmChartLegend;
import com.opensingular.bam.client.builder.amchart.AmChartValueAxes;
import com.opensingular.bam.client.builder.amchart.AmChartValueField;
import com.opensingular.bam.client.builder.amchart.AmSerialChartBuilder;

public abstract class AbstractSerialChart implements SingularChart {

    protected String category;
    protected List<AmChartValueField> values;

    protected boolean withLegend = false;

    public AbstractSerialChart() {
    }

    public AbstractSerialChart(String category, AmChartValueField... values) {
        this(category, Arrays.asList(values));
    }

    public AbstractSerialChart(String category, List<AmChartValueField> values) {
        this.values = values;
        this.category = category;
    }

    @Override
    public String getDefinition() {
        final AmSerialChartBuilder chartBuilder = new SingularChartBuilder()
                .newSerialChart()
                .theme("light")
                .startEffect("easeOutSine")
                .startDuration(0.5)
                .valueAxes(Collections.singletonList(new AmChartValueAxes()
                        .gridColor("#FFFFFF")
                        .gridAlpha(0.2)
                        .dashLength(0)))
                .gridAboveGraphs(true)
                .graphs(getGraphs())
                .chartCursor(new AmChartCursor()
                        .categoryBalloonEnabled(false)
                        .cursorAlpha(0)
                        .zoomable(false))
                .categoryField(category)
                .categoryAxis(new AmChartCategoryAxis()
                        .gridPosition("start")
                        .gridAlpha(0)
                        .tickPosition("start")
                        .tickLength(20)
                        .autoWrap(true));

        if (withLegend) {
            chartBuilder.legend(new AmChartLegend().useGraphSettings(true));
        }

        return chartBuilder.finish();
    }

    protected abstract Collection<AmChartGraph> getGraphs();

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<AmChartValueField> getValues() {
        return values;
    }

    public void setValues(List<AmChartValueField> values) {
        this.values = values;
    }

    public boolean isWithLegend() {
        return withLegend;
    }

    public void setWithLegend(boolean withLegend) {
        this.withLegend = withLegend;
    }
}
