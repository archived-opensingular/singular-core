package br.net.mirante.singular.bamclient.chart;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.net.mirante.singular.bamclient.builder.SingularChartBuilder;
import br.net.mirante.singular.bamclient.builder.amchart.AmChartCategoryAxis;
import br.net.mirante.singular.bamclient.builder.amchart.AmChartCursor;
import br.net.mirante.singular.bamclient.builder.amchart.AmChartGraph;
import br.net.mirante.singular.bamclient.builder.amchart.AmChartLegend;
import br.net.mirante.singular.bamclient.builder.amchart.AmChartValueAxes;
import br.net.mirante.singular.bamclient.builder.amchart.AmChartValueField;
import br.net.mirante.singular.bamclient.builder.amchart.AmSerialChartBuilder;

public abstract class AbstractSerialChart implements SingularChart {

    protected String category;
    protected List<AmChartValueField> values;

    protected boolean withLegend = false;

    public AbstractSerialChart() {
    }

    public AbstractSerialChart(String category, AmChartValueField... values) {
        this(category, Arrays.asList(values));
    }

    public AbstractSerialChart(String category, List<AmChartValueField> values) {
        this.values = values;
        this.category = category;
    }

    @Override
    public String getDefinition() {
        final AmSerialChartBuilder chartBuilder = new SingularChartBuilder()
                .newSerialChart()
                .theme("light")
                .startEffect("easeOutSine")
                .startDuration(0.5)
                .valueAxes(Collections.singletonList(new AmChartValueAxes()
                        .gridColor("#FFFFFF")
                        .gridAlpha(0.2)
                        .dashLength(0)))
                .gridAboveGraphs(true)
                .graphs(getGraphs())
                .chartCursor(new AmChartCursor()
                        .categoryBalloonEnabled(false)
                        .cursorAlpha(0)
                        .zoomable(false))
                .categoryField(category)
                .categoryAxis(new AmChartCategoryAxis()
                        .gridPosition("start")
                        .gridAlpha(0)
                        .tickPosition("start")
                        .tickLength(20)
                        .autoWrap(true));

        if (withLegend) {
            chartBuilder.legend(new AmChartLegend().useGraphSettings(true));
        }

        return chartBuilder.finish();
    }

    protected abstract Collection<AmChartGraph> getGraphs();

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<AmChartValueField> getValues() {
        return values;
    }

    public void setValues(List<AmChartValueField> values) {
        this.values = values;
    }

    public boolean isWithLegend() {
        return withLegend;
    }

    public void setWithLegend(boolean withLegend) {
        this.withLegend = withLegend;
    }
}
