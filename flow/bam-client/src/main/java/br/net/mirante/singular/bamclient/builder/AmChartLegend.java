package br.net.mirante.singular.bamclient.builder;


public class AmChartLegend extends AmChartObject<AmChartLegend> {

    public AmChartLegend useGraphSettings(boolean value) {
        return put("useGraphSettings", value);
    }

    @Override
    public AmChartLegend self() {
        return this;
    }
}
