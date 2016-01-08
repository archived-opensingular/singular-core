package br.net.mirante.singular.bamclient.builder;

public class AmSerialChartBuilder extends AmChartBuilder<AmSerialChartBuilder> {

    public AmSerialChartBuilder(AmChartBuilderContext amChartBuilderContext) {
        super(amChartBuilderContext);
        context.getjWriter().key("type").value("serial");
    }

    @Override
    public AmSerialChartBuilder self() {
        return this;
    }
}
