package br.net.mirante.singular.bamclient.builder.amchart;

import br.net.mirante.singular.bamclient.builder.JSONObjectMappper;

public class AmChartCursor extends JSONObjectMappper<AmChartCursor> {

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
