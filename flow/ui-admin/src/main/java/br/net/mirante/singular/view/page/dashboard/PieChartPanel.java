package br.net.mirante.singular.view.page.dashboard;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

import java.util.List;
import java.util.Map;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public abstract class PieChartPanel extends Panel {

    private String title;
    private String subtitle;
    private WebMarkupContainer barChartDiv;
    private List<Map<String, String>> dadosGrafico;

    public PieChartPanel(String id, String title, String subtitle) {
        super(id);
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        updateGraph();
        setOutputMarkupId(true);
        add(new Label("title", new ResourceModel(title)));
        add(new Label("subtitle", new ResourceModel(subtitle)));
        add(createPieChart());

    }

    private WebMarkupContainer createPieChart() {
        barChartDiv = new WebMarkupContainer("chart-div");
        barChartDiv.setOutputMarkupId(true);
        barChartDiv.add($b.onReadyScript(this::montarScript));
        return barChartDiv;
    }

    private void updateGraph() {
        dadosGrafico = retrieveData(25l);
    }

    protected abstract List<Map<String, String>> retrieveData(Long processId);

    private String parseToJson(List<Map<String, String>> dados) {
        return new JSONArray(dados).toString();
    }

    private CharSequence montarScript(Component comp) {
        String id = comp.getMarkupId();
        return "            AmCharts.makeChart( \"" + id + "\", { " +
                "                \"type\": \"pie\", " +
                "                \"angle\": 12, " +
                "                \"balloonText\": \"[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>\", " +
                "                \"depth3D\": 15, " +
                "                \"labelRadius\": 50, " +
                "                \"titleField\": \"NOME\", " +
                "                \"valueField\": \"MEAN\", " +
                "                \"allLabels\": [], " +
                "                \"balloon\": {}, " +
                "                \"legend\": { " +
                "                    \"align\": \"center\", " +
                "                    \"markerType\": \"circle\" " +
                "                }, " +
                "                \"titles\": [], " +
                "                \"dataProvider\":  " +
                parseToJson(dadosGrafico) +
                "                 " +
                "            } ); ";
    }

}
