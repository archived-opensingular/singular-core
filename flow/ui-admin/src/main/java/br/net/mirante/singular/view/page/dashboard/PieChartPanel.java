package br.net.mirante.singular.view.page.dashboard;

import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public abstract class PieChartPanel extends Panel {

    private String title;
    private String subtitle;
    private String valueField;
    private String titleField;
    private String titleGraph;

    private boolean isDonut;
    private boolean withFilter;
    private boolean withLegend;
    private PeriodType periodType;
    private WebMarkupContainer pieChartDiv;
    private List<Map<String, String>> dadosGrafico;

    public PieChartPanel(String id, String title, String subtitle, String valueField, String titleField) {
        this(id, title, subtitle, valueField, titleField, false, false);
    }

    public PieChartPanel(String id, String title, String subtitle, String titleGraph,
            String valueField, String titleField) {
        this(id, title, subtitle, valueField, titleField, titleGraph, false, false, false);
    }

    public PieChartPanel(String id, String title, String subtitle, String titleGraph,
            String valueField, String titleField, boolean withFilter, boolean isDonut) {
        this(id, title, subtitle, valueField, titleField, titleGraph, withFilter, false, isDonut);
    }

    public PieChartPanel(String id, String title, String subtitle, String valueField, String titleField,
            boolean withFilter, boolean isDonut) {
        this(id, title, subtitle, valueField, titleField, null, withFilter, false, isDonut);
    }

    public PieChartPanel(String id, String title, String subtitle, String valueField, String titleField,
            String titleGraph, boolean withFilter, boolean withLegend, boolean isDonut) {
        super(id);
        this.title = title;
        this.subtitle = subtitle;
        this.valueField = valueField;
        this.titleField = titleField;
        this.withFilter = withFilter;
        this.withLegend = withLegend;
        this.isDonut = isDonut;
        this.titleGraph = (titleGraph != null
                ? String.format("{\"id\": \"titleId\", \"size\": 16, \"text\": \"%s\"}", titleGraph) : null);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        periodType = PeriodType.YEARLY;
        updateGraph();
        setOutputMarkupId(true);
        add(new Label("title", new ResourceModel(title)));
        add(new Label("subtitle", new ResourceModel(subtitle)));
        createChartFilter();
        add(createPieChart());
    }

    private WebMarkupContainer createPieChart() {
        pieChartDiv = new WebMarkupContainer("chart-div");
        pieChartDiv.setOutputMarkupId(true);
        pieChartDiv.add($b.onReadyScript(this::montarScript));
        return pieChartDiv;
    }

    private void createChartFilter() {
        WebMarkupContainer actions = new WebMarkupContainer("_Filter");
        actions.add(createFilterOption("weekly"));
        actions.add(createFilterOption("monthly"));
        actions.add(createFilterOption("yearly"));
        add(actions);
        if (!withFilter) {
            actions.add($b.attrAppender("class", "hide", " "));
        }
    }

    private Component createFilterOption(String id) {
        Component filter = new WebMarkupContainer(id).add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                periodType = PeriodType.valueOfName(getComponent().getId());
                updateGraph();
                target.add(pieChartDiv);
            }
        });

        if (id.equalsIgnoreCase(periodType.getName())) {
            filter.add($b.classAppender("active"));

        }
        return filter;
    }

    private void updateGraph() {
        dadosGrafico = retrieveData(periodType);
    }

    protected abstract List<Map<String, String>> retrieveData(PeriodType periodType);

    private String parseToJson(List<Map<String, String>> dados) {
        return new JSONArray(dados).toString();
    }

    private CharSequence montarScript(Component comp) {
        return "            AmCharts.makeChart( \"" + comp.getMarkupId() + "\", {" +
                "                \"type\": \"pie\", " +
                "                \"angle\": 12," +
                "                \"marginTop\": -50," +
                "                \"balloonText\": \"[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>\"," +
                "                \"depth3D\": 15," +
                (isDonut ? "                \"innerRadius\": \"40%\"," : "") +
                "                \"labelRadius\": 50," +
                "                \"titleField\": \"" + titleField + "\"," +
                "                \"valueField\": \"" + valueField + "\"," +
                "                \"allLabels\": []," +
                "                \"balloon\": {}," +
                (withLegend ? "                \"legend\": {\"align\": \"center\", \"markerType\": \"circle\"}," : "") +
                "                \"titles\": [" + (titleGraph != null ? titleGraph : "") + "]," +
                "                \"dataProvider\": " + parseToJson(dadosGrafico) +
                "           });";
    }
}
