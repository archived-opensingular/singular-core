/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.chart;

import com.opensingular.bam.client.builder.SingularChartBuilder;

public class AreaChart implements SingularChart {

    private String categoryProperty;
    private String[] valueProperties;
    private String[] labels;

    public AreaChart() {
    }

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

    public String getCategoryProperty() {
        return categoryProperty;
    }

    public void setCategoryProperty(String categoryProperty) {
        this.categoryProperty = categoryProperty;
    }

    public String[] getValueProperties() {
        return valueProperties;
    }

    public void setValueProperties(String[] valueProperties) {
        this.valueProperties = valueProperties;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }
}
