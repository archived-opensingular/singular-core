/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.builder.amchart;

import com.opensingular.bam.client.builder.JSONBuilderContext;

public class AmPieChartBuilder extends AmChartBuilder<AmPieChartBuilder> {

    public AmPieChartBuilder(JSONBuilderContext context) {
        super(context);
        context.getJsonWriter().key("type").value("pie");
    }

    @Override
    public AmPieChartBuilder self() {
        return this;
    }

    public AmPieChartBuilder angle(Integer value) {
        return writeField("angle", value);
    }

    public AmPieChartBuilder marginTop(Integer value) {
        return writeField("marginTop", value);
    }

    public AmPieChartBuilder minRadius(Number value) {
        return writeField("minRadius", value);
    }

    public AmPieChartBuilder balloonText(String value) {
        return writeField("balloonText", value);
    }

    public AmPieChartBuilder labelRadius(Integer value) {
        return writeField("labelRadius", value);
    }

    public AmPieChartBuilder titleField(String value) {
        return writeField("titleField", value);
    }

    public AmPieChartBuilder valueField(String value) {
        return writeField("valueField", value);
    }

    public AmPieChartBuilder innerRadius(Integer value) {
        return writeField("innerRadius", value+"%");
    }

    public AmPieChartBuilder depth3D(Integer value) {
        return writeField("depth3D", value);
    }

}
