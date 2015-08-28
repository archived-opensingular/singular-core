package br.net.mirante.singular.view.page.dashboard;

import br.net.mirante.singular.service.PesquisaService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;
import java.time.Period;
import java.util.List;
import java.util.Map;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class BarChartPanel extends Panel {

    @Inject
    private PesquisaService pesquisaService;

    private List<Map<String, String>> dadosGrafico;
    private String title;
    private String subtitle;
    private PeriodType periodType;
    private WebMarkupContainer barChartDiv;

    public BarChartPanel(String id, String title, String subtitle) {
        super(id);
        this.title = title;
        this.subtitle = subtitle;
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
        add(createFilterOption("weekly"));
        add(createFilterOption("monthly"));
        add(createFilterOption("yearly"));
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
        dadosGrafico = pesquisaService.retrieveMeanTimeByProcess(periodType.getPeriod());
    }

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
                "                    \"balloonText\": \"[[category]]: <b>[[value]] dia(s)</b>\", " +
                "                    \"fillAlphas\": 0.8, " +
                "                    \"lineAlpha\": 0.2, " +
                "                    \"type\": \"column\", " +
                "                    \"valueField\": \"MEAN\" " +
                "                } ], " +
                "                \"chartCursor\": { " +
                "                    \"categoryBalloonEnabled\": false, " +
                "                    \"cursorAlpha\": 0, " +
                "                    \"zoomable\": false " +
                "                }, " +
                "                \"categoryField\": \"NOME\", " +
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
