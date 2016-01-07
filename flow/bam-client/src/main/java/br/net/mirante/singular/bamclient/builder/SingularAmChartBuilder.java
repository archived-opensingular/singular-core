package br.net.mirante.singular.bamclient.builder;


import java.io.IOException;

public class SingularAmChartBuilder extends AbstractAmChartBuilder {

    public SingularAmChartBuilder() {
        super(new AmChartBuilderContext());
    }

    public AmSerialChartBuilder newSerialChart() {
        try {
            context.getjGen().writeStartObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AmSerialChartBuilder(context);
    }

}
