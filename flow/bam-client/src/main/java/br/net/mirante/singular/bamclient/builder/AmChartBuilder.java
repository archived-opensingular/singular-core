package br.net.mirante.singular.bamclient.builder;

import java.util.Collection;
import java.util.Map;

import br.net.mirante.singular.bamclient.portlet.PortletFilterContext;


public abstract class AmChartBuilder<T extends AmChartBuilder> extends AbstractAmChartBuilder<T> {

    public AmChartBuilder(AmChartBuilderContext context) {
        super(context);
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

    public T dataProvider(ChartDataProvider dataProvider, PortletFilterContext filterContext) {
        context.getjWriter().key("dataProvider").array();
        for (Map<String, String> map : dataProvider.getData(filterContext)) {
            context.getjWriter().object();
            for (Map.Entry<String, String> entrySet : map.entrySet()) {
                context.getjWriter().key(entrySet.getKey()).value(entrySet.getValue());
            }
            context.getjWriter().endObject();
        }
        context.getjWriter().endArray();
        return self();
    }

}
