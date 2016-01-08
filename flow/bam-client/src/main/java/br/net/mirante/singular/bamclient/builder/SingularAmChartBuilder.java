package br.net.mirante.singular.bamclient.builder;


public class SingularAmChartBuilder extends AbstractAmChartBuilder<SingularAmChartBuilder> {

    public SingularAmChartBuilder() {
        super(new AmChartBuilderContext());
    }

    public AmSerialChartBuilder newSerialChart() {
        context.getjWriter().object();
        return new AmSerialChartBuilder(context);
    }

    @Override
    public SingularAmChartBuilder self() {
        return this;
    }
}
