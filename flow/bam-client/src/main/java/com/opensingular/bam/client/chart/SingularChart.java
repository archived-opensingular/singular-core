/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.chart;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AreaChart.class, name = "AreaChart"),
        @JsonSubTypes.Type(value = ColumnSerialChart.class, name = "ColumnSerialChart"),
        @JsonSubTypes.Type(value = LineSerialChart.class, name = "LineSerialChart"),
        @JsonSubTypes.Type(value = DonutPieChart.class, name = "DonutPieChart"),
        @JsonSubTypes.Type(value = PieChart.class, name = "PieChart")
})
public interface SingularChart extends Serializable {

    String getDefinition();
}
