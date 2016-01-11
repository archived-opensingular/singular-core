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
