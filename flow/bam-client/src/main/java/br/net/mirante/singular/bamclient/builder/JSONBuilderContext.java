package br.net.mirante.singular.bamclient.builder;

import java.io.StringWriter;

import org.json.JSONWriter;


public class JSONBuilderContext {

    private final JSONWriter jWriter;
    private final StringWriter writer = new StringWriter();

    public JSONBuilderContext() {
        jWriter = new JSONWriter(writer);
    }

    public JSONWriter getjWriter() {
        return jWriter;
    }

    public StringWriter getWriter() {
        return writer;
    }
}
