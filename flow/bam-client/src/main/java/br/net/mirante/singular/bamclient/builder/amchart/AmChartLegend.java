/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.builder.amchart;


import br.net.mirante.singular.bamclient.builder.JSONObjectMappper;

public class AmChartLegend extends JSONObjectMappper<AmChartLegend> {

    public AmChartLegend useGraphSettings(boolean value) {
        return put("useGraphSettings", value);
    }

    @Override
    public AmChartLegend self() {
        return this;
    }
}
