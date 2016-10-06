/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.builder.amchart;

import java.util.Collection;

import com.opensingular.bam.client.builder.AbstractJSONBuilder;
import com.opensingular.bam.client.builder.JSONBuilderContext;


public abstract class AmChartBuilder<T extends AmChartBuilder<T>> extends AbstractJSONBuilder<T> {

    public AmChartBuilder(JSONBuilderContext context) {
        super(context);
        context.getJsonWriter().object();
    }

    public T startEffect(String value) {
        return writeField("startEffect", value);
    }

    public T categoryField(String value) {
        return writeField("categoryField", value);
    }

    public T startDuration(Number value) {
        return writeField("startDuration", value);
    }

    public T theme(String value) {
        return writeField("theme", value);
    }

    public T gridAboveGraphs(boolean value) {
        return writeField("gridAboveGraphs", value);
    }

    public T graphs(Collection<AmChartGraph> graphs) {
        return writeArray("graphs", graphs);
    }

    public T categoryAxis(AmChartCategoryAxis value) {
        return writeNamedObject("categoryAxis", value);
    }

    public T legend(AmChartLegend value) {
        return writeNamedObject("legend", value);
    }

    public T chartCursor(AmChartCursor value) {
        return writeNamedObject("chartCursor", value);
    }

    public T valueAxes(Collection<AmChartValueAxes> value) {
        return writeArray("valueAxes", value);
    }

    public T titles(String... titles) {
        return writeArray("titles", titles);
    }

}
