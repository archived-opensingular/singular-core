package br.net.mirante.singular.bamclient.builder.amchart;

import br.net.mirante.singular.bamclient.builder.JSONObjectMappper;

public class AmChartValueAxes extends JSONObjectMappper<AmChartValueAxes> {

    public AmChartValueAxes gridColor(String value) {
        return put("gridColor", value);
    }

    public AmChartValueAxes gridAlpha(Number value) {
        return put("gridAlpha", value);
    }

    public AmChartValueAxes dashLength(Number value) {
        return put("dashLength", value);
    }

    @Override
    public AmChartValueAxes self() {
        return this;
    }

}
