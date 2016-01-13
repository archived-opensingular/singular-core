package br.net.mirante.singular.bamclient.builder;


import br.net.mirante.singular.bamclient.builder.amchart.AmPieChartBuilder;
import br.net.mirante.singular.bamclient.builder.amchart.AmSerialChartBuilder;
import br.net.mirante.singular.bamclient.builder.morris.MorrisAreaChart;

public class SingularChartBuilder extends AbstractJSONBuilder<SingularChartBuilder> {

    public SingularChartBuilder() {
        super(new JSONBuilderContext());
    }

    public AmSerialChartBuilder newSerialChart() {
        context.getjWriter().object();
        return new AmSerialChartBuilder(context);
    }

    public AmPieChartBuilder newPieChart() {
        context.getjWriter().object();
        return new AmPieChartBuilder(context);
    }

    public MorrisAreaChart newAreaChart() {
        context.getjWriter().object();
        return new MorrisAreaChart(context);
    }

    @Override
    public SingularChartBuilder self() {
        return this;
    }
}
