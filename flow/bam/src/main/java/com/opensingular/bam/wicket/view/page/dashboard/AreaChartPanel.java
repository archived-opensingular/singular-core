/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view.page.dashboard;

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

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public abstract class AreaChartPanel extends Panel {

    private String title;
    private String subtitle;
    private String titleField;
    private List<Pair<String, String>> valuesField;

    private boolean withFilter;
    private boolean withLegend;
    private PeriodType periodType;
    private WebMarkupContainer areaChartDiv;
    private List<Map<String, String>> dadosGrafico;

    public AreaChartPanel(String id, String title, String subtitle,
            Pair<String, String> graphInfo, String titleField,
            boolean withFilter, boolean withLegend) {
        super(id);
        this.title = title;
        this.subtitle = subtitle;
        this.titleField = titleField;
        this.valuesField = new ArrayList<>();
        this.valuesField.add(graphInfo);
        this.withFilter = withFilter;
        this.withLegend = withLegend;
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

    public AreaChartPanel addGraph(String valueField, String title) {
        this.valuesField.add(new ImmutablePair<>(valueField, title));
        return this;
    }

    private WebMarkupContainer createPieChart() {
        areaChartDiv = new WebMarkupContainer("chart-div");
        areaChartDiv.setOutputMarkupId(true);
        areaChartDiv.add($b.onReadyScript(this::montarScript));
        return areaChartDiv;
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
                target.add(areaChartDiv);
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

    private String montarValueKeys() {
        String ykeys = " ykeys: [%s],";
        StringBuilder ykeysElements = new StringBuilder();
        for (Pair<String, String> element : valuesField) {
            ykeysElements.append("'").append(element.getLeft()).append("',");
        }
        return String.format(ykeys, ykeysElements.substring(0, ykeysElements.length() - 1));
    }

    private String montarValueLabels() {
        String ykeys = " labels: [%s],";
        StringBuilder ykeysElements = new StringBuilder();
        for (Pair<String, String> element : valuesField) {
            ykeysElements.append("'").append(element.getRight()).append("',");
        }
        return String.format(ykeys, ykeysElements.substring(0, ykeysElements.length() - 1));
    }

    private CharSequence montarScript(Component comp) {
        return "Morris.Area({"
                + "element: '" + comp.getMarkupId() + "',"
                + " padding: 0,"
                + " behaveLikeLine: false,"
                + " idEnabled: false,"
                + " gridLineColor: false,"
                + " axes: false,"
                + " fillOpacity: 1,"
                + " data: "
                + parseToJson(dadosGrafico) + ","
                + " lineColors: ['#399a8c', '#92e9dc'],"
                + " xkey: '" + titleField + "',"
                + montarValueKeys()
                + montarValueLabels()
                + " pointSize: 0,"
                + " lineWidth: 0,"
                + " hideHover: '" + (withLegend ? "auto" : "always") + "',"
                + " resize: true,"
                + " behaveLikeLine: false,"
                + " dateFormat: function (x) {"
                + "   var months = ['JAN', 'FEV', 'MAR', 'ABR', 'MAI', 'JUN', 'JUL', 'AGO', 'SET', 'OUT', 'NOV', 'DEZ'];"
                + "   var value = new Date(x);"
                + "   return months[value.getMonth()] + '/' + value.getFullYear().toString().substring(2, 4);"
                + "}"
                + "});";
    }
}
