/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.builder.amchart;

import br.net.mirante.singular.bamclient.builder.JSONObjectMappper;

public class AmChartGraph extends JSONObjectMappper<AmChartGraph> {

    public AmChartGraph fillAlphas(Number value) {
        return put("fillAlphas", value);
    }

    public AmChartGraph lineAlpha(Number value) {
        return put("lineAlpha", value);
    }

    public AmChartGraph type(String value) {
        return put("type", value);
    }

    public AmChartGraph valueField(String value) {
        return put("valueField", value);
    }

    public AmChartGraph balloonText(String value) {
        return put("balloonText", value);
    }

    public AmChartGraph bullet(String value) {
        return put("bullet", value);
    }

    public AmChartGraph title(String value) {
        return put("title", value);
    }
    @Override
    public AmChartGraph self() {
        return this;
    }
}
