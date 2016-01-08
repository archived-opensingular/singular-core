package br.net.mirante.singular.bamclient.builder;

public class AmChartCategoryAxis extends AmChartObject<AmChartCategoryAxis> {

    public AmChartCategoryAxis gridPosition(String value) {
        return put("gridPosition", value);
    }

    public AmChartCategoryAxis gridAlpha(Number value) {
        return put("gridAlpha", value);
    }

    public AmChartCategoryAxis tickPosition(String value) {
        return put("tickPosition", value);
    }

    public AmChartCategoryAxis tickLength(Number value) {
        return put("tickLength", value);
    }

    public AmChartCategoryAxis autoWrap(boolean value) {
        return put("autoWrap", value);
    }

    @Override
    public AmChartCategoryAxis self() {
        return this;
    }
}
