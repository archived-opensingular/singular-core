package br.net.mirante.singular.bamclient.builder;


import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractAmChartBuilder<T extends AbstractAmChartBuilder> implements SelfReference<T> {

    protected AmChartBuilderContext context;

    public AbstractAmChartBuilder(AmChartBuilderContext context) {
        this.context = context;
    }

    protected T writeField(String key, Object value) {
        context.getjWriter().key(key).value(value);
        return self();
    }

    protected <X extends AmChartObject<?>> T writeObject(X value) {
        context.getjWriter().object();
        for (Map.Entry<String, Object> entry : value.getObjectMap().entrySet()) {
            context.getjWriter().key(entry.getKey()).value(entry.getValue());
        }
        context.getjWriter().endObject();
        return self();
    }

    protected <X extends AmChartObject<?>> T writeNamedObject(String name, X value) {
        context.getjWriter().key(name);
        return writeObject(value);
    }

    protected <X extends AmChartObject<?>> T writeArray(String name, Collection<X> amChartObjects) {
        context.getjWriter().key(name).array();
        for (AmChartObject<?> amChartObject : amChartObjects) {
            writeObject(amChartObject);
        }
        context.getjWriter().endArray();
        return self();
    }

    public String finish() {
        try {
            context.getjWriter().endObject();
            context.getWriter().flush();
            context.getWriter().close();
            return context.getWriter().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Erro ao criar JSON Generator");
    }

}
