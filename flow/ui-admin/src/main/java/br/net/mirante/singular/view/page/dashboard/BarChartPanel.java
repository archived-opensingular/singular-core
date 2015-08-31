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

public abstract class BarChartPanel extends Panel {

    private String title;
    private String subtitle;
    private String valueField;
    private String categoryField;
    private String balloonTextsuffix;

    private boolean withFilter;
    private PeriodType periodType;
    private WebMarkupContainer barChartDiv;
    private List<Map<String, String>> dadosGrafico;

    public BarChartPanel(String id, String title, String subtitle, String valueField, String categoryField,
            boolean withFilter) {
        this(id, title, subtitle, valueField, categoryField, "", withFilter);
    }

    public BarChartPanel(String id, String title, String subtitle, String valueField, String categoryField) {
        this(id, title, subtitle, valueField, categoryField, "", false);
    }

    public BarChartPanel(String id, String title, String subtitle,
            String valueField, String categoryField, String balloonTextsuffix, boolean withFilter) {
        super(id);
        this.title = title;
        this.subtitle = subtitle;
        this.valueField = valueField;
        this.categoryField = categoryField;
        this.balloonTextsuffix = balloonTextsuffix;
        this.withFilter = withFilter;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        periodType = PeriodType.WEEKLY;
        updateGraph();
        setOutputMarkupId(true);
        add(new Label("title", new ResourceModel(title)));
        add(new Label("subtitle", new ResourceModel(subtitle)));
        createChartFilter();
        add(createBarChart());
    }

    private WebMarkupContainer createBarChart() {
        barChartDiv = new WebMarkupContainer("chart-div");
        barChartDiv.setOutputMarkupId(true);
        barChartDiv.add($b.onReadyScript(this::montarScript));
        return barChartDiv;
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
                target.add(barChartDiv);
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
        String id = comp.getMarkupId();
        return "            AmCharts.makeChart( \"" + id + "\", { " +
                "                \"type\": \"serial\", " +
                "                \"theme\": \"light\", " +
                "                \"dataProvider\":  " +
                parseToJson(dadosGrafico) +
                "                , " +
                "                \"valueAxes\": [ { " +
                "                    \"gridColor\": \"#FFFFFF\", " +
                "                    \"gridAlpha\": 0.2, " +
                "                    \"dashLength\": 0 " +
                "                } ], " +
                "                \"gridAboveGraphs\": true, " +
                "                \"startDuration\": 1, " +
                "                \"graphs\": [ { " +
                "                    \"balloonText\": \"[[category]]: <b>[[value]]" + balloonTextsuffix + "</b>\", " +
                "                    \"fillAlphas\": 0.8, " +
                "                    \"lineAlpha\": 0.2, " +
                "                    \"type\": \"column\", " +
                "                    \"valueField\": \"" + valueField + "\" " +
                "                } ], " +
                "                \"chartCursor\": { " +
                "                    \"categoryBalloonEnabled\": false, " +
                "                    \"cursorAlpha\": 0, " +
                "                    \"zoomable\": false " +
                "                }, " +
                "                \"categoryField\": \"" + categoryField + "\", " +
                "                \"categoryAxis\": { " +
                "                    \"gridPosition\": \"start\", " +
                "                    \"gridAlpha\": 0, " +
                "                    \"tickPosition\": \"start\", " +
                "                    \"tickLength\": 20, " +
                "                    \"autoWrap\": true " +
                "                }, " +
                "                \"export\": { " +
                "                    \"enabled\": true " +
                "                } " +
                "            } ); ";
    }
}
