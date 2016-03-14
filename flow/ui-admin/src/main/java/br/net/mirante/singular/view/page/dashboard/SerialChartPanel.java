/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.view.page.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public abstract class SerialChartPanel extends Panel {

    public static final String DEFAULT_GRAPH_TYPE = "column";
    private String title;
    private String subtitle;
    private String categoryField;
    private List<Pair<String, String>> valuesField;
    private String balloonTextsuffix;
    private String graphType;
    private String titleGraph;

    private boolean withFilter;
    private boolean withLegend;
    private PeriodType periodType;
    private WebMarkupContainer barChartDiv;
    private List<Map<String, String>> dadosGrafico;

    public SerialChartPanel(String id, String title, String subtitle, String valueField, String categoryField,
            boolean withFilter) {
        this(id, title, subtitle, valueField, categoryField, "", withFilter);
    }

    public SerialChartPanel(String id, String title, String subtitle, String valueField, String categoryField) {
        this(id, title, subtitle, valueField, categoryField, "", false);
    }

    public SerialChartPanel(String id, String title, String subtitle, String valueField, String categoryField,
            String graphType) {
        this(id, title, subtitle, valueField, categoryField, "", false);
        this.graphType = graphType;
    }

    public SerialChartPanel(String id, String title, String subtitle, String valueField, String categoryField,
            String graphType, String titleGraph) {
        this(id, title, subtitle, valueField, categoryField, "", false);
        this.graphType = graphType;
        this.titleGraph = (titleGraph != null
                ? String.format("{\"id\": \"titleId\", \"size\": 16, \"text\": \"%s\"}", titleGraph) : null);
    }

    public SerialChartPanel(String id, String title, String subtitle, Pair<String, String> graphInfo,
            String categoryField, String graphType) {
        this(id, title, subtitle, graphInfo, categoryField, "", false, false);
        this.graphType = graphType;
    }

    public SerialChartPanel(String id, String title, String subtitle,
            String valueField, String categoryField, String balloonTextsuffix, boolean withFilter) {
        this(id, title, subtitle, new ImmutablePair<>(valueField, valueField),
                categoryField, balloonTextsuffix, withFilter, false);
    }

    public SerialChartPanel(String id, String title, String subtitle,
            Pair<String, String> graphInfo, String categoryField, String balloonTextsuffix,
            boolean withFilter, boolean withLegend) {
        super(id);
        this.title = title;
        this.subtitle = subtitle;
        this.valuesField = new ArrayList<>();
        this.valuesField.add(graphInfo);
        this.categoryField = categoryField;
        this.balloonTextsuffix = balloonTextsuffix;
        this.withFilter = withFilter;
        this.withLegend = withLegend;
        this.graphType = DEFAULT_GRAPH_TYPE;
        this.titleGraph= null;
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
        add(createBarChart());
    }

    public SerialChartPanel addGraph(String valueField) {
        this.valuesField.add(new ImmutablePair<>(valueField, valueField));
        return this;
    }

    public SerialChartPanel addGraph(String valueField, String title) {
        this.valuesField.add(new ImmutablePair<>(valueField, title));
        return this;
    }

    public SerialChartPanel addLegend() {
        this.withLegend = true;
        return this;
    }

    protected abstract List<Map<String, String>> retrieveData(PeriodType periodType);

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

    private String parseToJson(List<Map<String, String>> dados) {
        return new JSONArray(dados).toString();
    }

    private static final String DEFAULT_GRAPH_PATTERN =
            "{\"balloonText\": \"[[category]]: <b>[[value]]%s</b>\"," +
            " \"fillAlphas\": 0.8," +
            " \"lineAlpha\": 0.2," +
            " \"type\": \"%s\"," +
            " \"valueField\": \"%s\"," +
            " \"title\": \"%s\"" +
            "},";
    private static final String GRAPH_PATTERN =
            "{\"bullet\": \"square\"," +
            " \"type\": \"%s\"," +
            " \"valueField\": \"%s\"," +
            " \"title\": \"%s\"" +
            "},";

    private String montrarGraphs() {
        StringBuilder graphsJSON = new StringBuilder();
        if (graphType.equalsIgnoreCase(DEFAULT_GRAPH_TYPE)) {
            valuesField.forEach(valueField -> graphsJSON
                    .append(String.format(DEFAULT_GRAPH_PATTERN, balloonTextsuffix, graphType,
                            valueField.getLeft(), valueField.getRight())));
        } else {
            valuesField.forEach(valueField -> graphsJSON
                    .append(String.format(GRAPH_PATTERN, graphType, valueField.getLeft(), valueField.getRight())));
        }
        return graphsJSON.substring(0, graphsJSON.length() - 1);
    }

    private CharSequence montarScript(Component comp) {
        return "            AmCharts.makeChart(\"" + comp.getMarkupId() + "\", {" +
               "                \"type\": \"serial\"," +
               "                \"theme\": \"light\"," +
               "                \"dataProvider\":" +
               parseToJson(dadosGrafico) + "," +
               "                \"valueAxes\": [{" +
               "                    \"gridColor\": \"#FFFFFF\"," +
               "                    \"gridAlpha\": 0.2," +
               "                    \"dashLength\": 0" +
               "                }]," +
               "                \"gridAboveGraphs\": true," +
               "                \"startDuration\": 1," +
               "                \"graphs\": [" +
               montrarGraphs() +
               "                ]," +
               "                \"chartCursor\": {" +
               "                    \"categoryBalloonEnabled\": false," +
               "                    \"cursorAlpha\": 0," +
               "                    \"zoomable\": false" +
               "                }," +
               "                \"categoryField\": \"" + categoryField + "\"," +
               "                \"categoryAxis\": {" +
               "                    \"gridPosition\": \"start\"," +
               "                    \"gridAlpha\": 0," +
               "                    \"tickPosition\": \"start\"," +
               "                    \"tickLength\": 20," +
               "                    \"autoWrap\": true" +
               "                }," +
               (withLegend ? "                \"legend\": {\"useGraphSettings\": true}," : "") +
               "                \"titles\": [" + (titleGraph != null ? titleGraph : "") + "]," +
               "                \"export\": {" +
               "                    \"enabled\": true" +
               "                }" +
               "            });";
    }
}
