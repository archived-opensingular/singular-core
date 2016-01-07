package br.net.mirante.singular.bamclient.builder;

import java.io.IOException;
import java.util.Map;

public abstract class AmChartBuilder<T extends AmChartBuilder> extends AbstractAmChartBuilder {

    public AmChartBuilder(AmChartBuilderContext context) {
        super(context);
    }

    public T setCategoryField(String categoryField) {
        try {
            context.getjGen().writeStringField("categoryField", categoryField);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (T) this;
    }

    public T setTheme(String theme) {
        try {
            context.getjGen().writeStringField("theme", theme);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (T) this;
    }

    public T setDataProvider(AmChartDataProvider dataProvider) {
        try {
            context.getjGen().writeArrayFieldStart("dataProvider");
            for (Map<String, String> map : dataProvider.getDataList()) {
                context.getjGen().writeStartObject();
                for (Map.Entry<String, String> entrySet : map.entrySet()) {
                    context.getjGen().writeStringField(entrySet.getKey(), entrySet.getValue());
                }
                context.getjGen().writeEndObject();
            }
            context.getjGen().writeEndArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (T) this;
    }

    public T setGraph(AmChartGraph amChartGraph) {
        try {
            context.getjGen().writeArrayFieldStart("graphs");
            context.getjGen().writeStartObject();
            for (Map.Entry<String, String> entrySet : amChartGraph.getProperties().entrySet()) {
                context.getjGen().writeStringField(entrySet.getKey(), entrySet.getValue());
            }
            context.getjGen().writeEndObject();
            context.getjGen().writeEndArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (T) this;
    }
}
