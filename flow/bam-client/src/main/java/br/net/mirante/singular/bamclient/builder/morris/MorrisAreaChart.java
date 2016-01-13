package br.net.mirante.singular.bamclient.builder.morris;

import br.net.mirante.singular.bamclient.builder.AbstractJSONBuilder;
import br.net.mirante.singular.bamclient.builder.JSONBuilderContext;

public class MorrisAreaChart extends AbstractJSONBuilder<MorrisAreaChart> {

    public MorrisAreaChart(JSONBuilderContext context) {
        super(context);
        context.getjWriter().key("type").value("Area");
    }

    public MorrisAreaChart padding(Integer value) {
        return writeField("padding", value);
    }

    public MorrisAreaChart behaveLikeLine(Boolean value) {
        return writeField("behaveLikeLine", value);
    }

    public MorrisAreaChart idEnabled(Boolean value) {
        return writeField("idEnabled", value);
    }

    public MorrisAreaChart gridLineColor(Boolean value) {
        return writeField("gridLineColor", value);
    }

    public MorrisAreaChart axes(Boolean value) {
        return writeField("axes", value);
    }

    public MorrisAreaChart fillOpacity(Double value) {
        return writeField("fillOpacity", value);
    }

    public MorrisAreaChart xkey(String value) {
        return writeField("xkey", value);
    }

    public MorrisAreaChart pointSize(Integer value) {
        return writeField("pointSize", value);
    }

    public MorrisAreaChart lineWidth(Integer value) {
        return writeField("lineWidth", value);
    }

    public MorrisAreaChart hideHover(String value) {
        return writeField("hideHover", value);
    }

    public MorrisAreaChart resize(Boolean value) {
        return writeField("resize", value);
    }

    public MorrisAreaChart lineColors(String... value) {
        return writeArray("lineColors", value);
    }

    public MorrisAreaChart ykeys(String... value) {
        return writeArray("ykeys", value);
    }

    public MorrisAreaChart labels(String... value) {
        return writeArray("labels", value);
    }

    @Override
    public MorrisAreaChart self() {
        return this;
    }
}
