/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.bamclient.builder;


import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opensingular.singular.bamclient.util.SelfReference;

public abstract class AbstractJSONBuilder<T extends AbstractJSONBuilder<T>> implements SelfReference<T> {

    protected final JSONBuilderContext context;

    protected final Logger logger = LoggerFactory.getLogger(AbstractJSONBuilder.class);

    public AbstractJSONBuilder(JSONBuilderContext context) {
        this.context = context;
    }

    protected final T writeField(String key, Object value) {
        if (value != null) {
            context.getJsonWriter().key(key).value(value);
        }
        return self();
    }

    @SafeVarargs
    protected final <O> T writeArray(String property, O... values) {
        if (values != null) {
            context.getJsonWriter().key(property).array();
            for (O o : values) {
                context.getJsonWriter().value(o);
            }
            context.getJsonWriter().endArray();
        }
        return self();
    }

    protected final <M extends JSONObjectMappper<M>> T writeObject(M mapper) {
        if (mapper != null) {
            context.getJsonWriter().object();
            for (Map.Entry<String, Object> entry : mapper.getObjectMap().entrySet()) {
                context.getJsonWriter().key(entry.getKey()).value(entry.getValue());
            }
            context.getJsonWriter().endObject();
        }
        return self();
    }

    protected final <M extends JSONObjectMappper<M>> T writeNamedObject(String name, M mapper) {
        if (mapper != null) {
            context.getJsonWriter().key(name);
        }
        return writeObject(mapper);
    }

    protected final <M extends JSONObjectMappper<M>> T writeArray(String name, Collection<M> mappers) {
        if (mappers != null) {
            context.getJsonWriter().key(name).array();
            for (M mapper : mappers) {
                writeObject(mapper);
            }
            context.getJsonWriter().endArray();
        }
        return self();
    }

    public final String finish() {
        try {
            context.getJsonWriter().endObject();
            context.getWriter().flush();
            context.getWriter().close();
            return context.getWriter().toString();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        throw new RuntimeException("Erro ao criar JSON Generator");
    }

}
