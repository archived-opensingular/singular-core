/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.bamclient.builder.amchart;


import org.opensingular.singular.bamclient.builder.JSONObjectMappper;

public class AmChartLegend extends JSONObjectMappper<AmChartLegend> {

    public AmChartLegend useGraphSettings(boolean value) {
        return put("useGraphSettings", value);
    }

    @Override
    public AmChartLegend self() {
        return this;
    }
}
