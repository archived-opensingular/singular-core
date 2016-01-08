package br.net.mirante.singular.bamclient.builder;

public class AmChartValueAxes extends AmChartObject<AmChartValueAxes> {

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
