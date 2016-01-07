package br.net.mirante.singular.bamclient.builder;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class AmChartBuilderContext {

    private final JsonGenerator jGen;
    private final StringWriter writer = new StringWriter();

    public AmChartBuilderContext() {
        try {
            final JsonFactory jsonFactory = new JsonFactory();
            jGen = jsonFactory.createGenerator(writer);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar JSON Generator");
        }
    }

    public JsonGenerator getjGen() {
        return jGen;
    }

    public StringWriter getWriter() {
        return writer;
    }
}
