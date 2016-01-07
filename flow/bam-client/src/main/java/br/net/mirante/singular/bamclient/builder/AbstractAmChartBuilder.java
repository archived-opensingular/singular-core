package br.net.mirante.singular.bamclient.builder;


import java.io.IOException;

public abstract class AbstractAmChartBuilder {

    protected AmChartBuilderContext context;

    public AbstractAmChartBuilder(AmChartBuilderContext context) {
        this.context = context;
    }

    public String finish() {
        try {
            context.getjGen().writeEndObject();
            context.getjGen().close();
            context.getWriter().flush();
            context.getWriter().close();
            return context.getWriter().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Erro ao criar JSON Generator");
    }

}
