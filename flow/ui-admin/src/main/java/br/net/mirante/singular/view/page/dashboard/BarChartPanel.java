package br.net.mirante.singular.view.page.dashboard;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import br.net.mirante.singular.service.PesquisaService;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class BarChartPanel extends Panel {

    @Inject
    private PesquisaService pesquisaService;

    private List<Map<String, String>> dadosGrafico;
    private String title;
    private String subtitle;

    public BarChartPanel(String id, String title, String subtitle) {
        super(id);
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        dadosGrafico = pesquisaService.retrieveMeanTimeByProcess();
        add(new Label("title", new ResourceModel(title)));
        add(new Label("subtitle", new ResourceModel(subtitle)));
        add(createChartFilter());
        add(createBarChart());

    }

    private WebMarkupContainer createBarChart() {
        WebMarkupContainer barChartDiv = new WebMarkupContainer("chart-div");
        barChartDiv.setOutputMarkupId(true);
        barChartDiv.add($b.onReadyScript(this::montarScript));
        return barChartDiv;
    }

    private Component createChartFilter() {
        Form<Object> form = new Form<>("chart-form");
        List<String> fruits = Arrays.asList("Semanal", "Mensal", "Anual");
        RadioChoice<String> chartFilter = new RadioChoice<>("chart-filter", Model.of(""), fruits);
        chartFilter.add($b.attrAppender("class", "btn-group btn-group-devided", " "));
        chartFilter.add($b.attrAppender("data-toggle", "buttons", " "));
        form.add(chartFilter);
        return form;
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
                "                    \"balloonText\": \"[[category]]: <b>[[value]]</b>\", " +
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
