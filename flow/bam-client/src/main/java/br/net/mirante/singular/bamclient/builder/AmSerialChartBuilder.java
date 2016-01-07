package br.net.mirante.singular.bamclient.builder;

import java.io.IOException;

public class AmSerialChartBuilder extends AmChartBuilder<AmSerialChartBuilder> {

    public AmSerialChartBuilder(AmChartBuilderContext amChartBuilderContext) {
        super(amChartBuilderContext);
        try {
            context.getjGen().writeStringField("type", "serial");
        } catch (IOException ex) {

        }
    }
}
