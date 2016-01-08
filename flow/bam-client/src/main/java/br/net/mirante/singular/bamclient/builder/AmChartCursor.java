package br.net.mirante.singular.bamclient.builder;

public class AmChartCursor extends AmChartObject<AmChartCursor> {

    public AmChartCursor categoryBalloonEnabled(boolean value) {
        return put("categoryBalloonEnabled", value);
    }

    public AmChartCursor cursorAlpha(Number value) {
        return put("cursorAlpha", value);
    }

    public AmChartCursor zoomable(boolean value) {
        return put("zoomable", value);
    }

    @Override
    public AmChartCursor self() {
        return this;
    }

}
