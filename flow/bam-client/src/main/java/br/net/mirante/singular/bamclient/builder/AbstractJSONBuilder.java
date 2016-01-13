package br.net.mirante.singular.bamclient.builder;


import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import br.net.mirante.singular.bamclient.util.SelfReference;

public abstract class AbstractJSONBuilder<T extends AbstractJSONBuilder> implements SelfReference<T> {

    protected JSONBuilderContext context;

    public AbstractJSONBuilder(JSONBuilderContext context) {
        this.context = context;
    }

    protected T writeField(String key, Object value) {
        if(value != null) {
            context.getjWriter().key(key).value(value);
        }
        return self();
    }

    protected T writeArray(String property, Object... values) {
        if(values != null) {
            context.getjWriter().key(property).array();
            for (Object o : values) {
                context.getjWriter().value(o);
            }
            context.getjWriter().endArray();
        }
        return self();
    }

    protected <X extends JSONObjectMappper<?>> T writeObject(X value) {
        if(value != null) {
            context.getjWriter().object();
            for (Map.Entry<String, Object> entry : value.getObjectMap().entrySet()) {
                context.getjWriter().key(entry.getKey()).value(entry.getValue());
            }
            context.getjWriter().endObject();
        }
        return self();
    }

    protected <X extends JSONObjectMappper<?>> T writeNamedObject(String name, X value) {
        if(value != null) {
            context.getjWriter().key(name);
        }
        return writeObject(value);
    }

    protected <X extends JSONObjectMappper<?>> T writeArray(String name, Collection<X> amChartObjects) {
        if(amChartObjects != null) {
            context.getjWriter().key(name).array();
            for (JSONObjectMappper<?> JSONObjectMappper : amChartObjects) {
                writeObject(JSONObjectMappper);
            }
            context.getjWriter().endArray();
        }
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
