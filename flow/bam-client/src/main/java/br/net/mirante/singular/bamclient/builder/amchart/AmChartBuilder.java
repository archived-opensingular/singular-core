package br.net.mirante.singular.bamclient.builder.amchart;

import java.util.Collection;

import br.net.mirante.singular.bamclient.builder.AbstractJSONBuilder;
import br.net.mirante.singular.bamclient.builder.JSONBuilderContext;


public abstract class AmChartBuilder<T extends AmChartBuilder> extends AbstractJSONBuilder<T> {

    public AmChartBuilder(JSONBuilderContext context) {
        super(context);
    }

    public T startEffect(String value) {
        return writeField("startEffect", value);
    }

    public T categoryField(String value) {
        return writeField("categoryField", value);
    }

    public T startDuration(Number value) {
        return writeField("startDuration", value);
    }

    public T theme(String value) {
        return writeField("theme", value);
    }

    public T gridAboveGraphs(boolean value) {
        return writeField("gridAboveGraphs", value);
    }

    public T graphs(Collection<AmChartGraph> graphs) {
        return writeArray("graphs", graphs);
    }

    public T categoryAxis(AmChartCategoryAxis value) {
        return writeNamedObject("categoryAxis", value);
    }

    public T legend(AmChartLegend value) {
        return writeNamedObject("legend", value);
    }

    public T chartCursor(AmChartCursor value) {
        return writeNamedObject("chartCursor", value);
    }

    public T valueAxes(Collection<AmChartValueAxes> value) {
        return writeArray("valueAxes", value);
    }

    public T titles(Collection<String> titles) {
        context.getjWriter().key("titles").array();
        titles.forEach(t -> context.getjWriter().value(t));
        context.getjWriter().endArray();
        return self();
    }

}
