/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.mapper.datetime;

import org.opensingular.singular.form.view.SViewDateTime;
import org.opensingular.singular.form.wicket.behavior.InputMaskBehavior;
import org.opensingular.singular.form.wicket.behavior.InputMaskBehavior.Masks;
import org.opensingular.singular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.singular.form.wicket.model.SIDateTimeModel;
import org.opensingular.singular.util.wicket.behavior.DatePickerInitBehaviour;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSContainer;
import org.opensingular.singular.util.wicket.bootstrap.layout.TemplatePanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class DateTimeContainer extends BSContainer<DateTimeContainer> {

    private final ISInstanceAwareModel<Date> model;
    private final SViewDateTime dateTimerView;

    public DateTimeContainer(String id, ISInstanceAwareModel<Date> model, SViewDateTime dateTimerView) {
        super(id);
        this.model = model;
        this.dateTimerView = dateTimerView;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final TemplatePanel template = buildTemplatePanel();
        template.add(buildDateField());
        template.add(buildTimeField());
    }

    protected Component buildDateField() {
        return new TextField<>("date", new SIDateTimeModel.DateModel(model))
                .add(new DatePickerInitBehaviour())
                .add(new InputMaskBehavior(Masks.FULL_DATE));
    }

    protected TextField<String> buildTimeField() {
        final TextField<String> time = new TextField<>("time", new SIDateTimeModel.TimeModel(model));
        time.add(new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                final String script = String.format("$('#%s').timepicker(%s)", component.getMarkupId(true), getJSONParams());
                response.render(OnDomReadyHeaderItem.forScript(script));
            }
        });
        time.add(new InputMaskBehavior(Masks.TIME));
        return time;
    }

    protected TemplatePanel buildTemplatePanel() {
        return newTemplateTag(tt -> {
            final StringBuilder templateBuildr = new StringBuilder();
            templateBuildr.append(" <div class='input-group'> ");
            templateBuildr.append("    <input wicket:id='date' type='text' class='form-control date date-picker' ");
            templateBuildr.append("      data-date-format='dd/mm/yyyy' data-date-start-date='01/01/1900' ");
            templateBuildr.append("      data-date-end-date='31/12/2999' data-date-start-view='days' ");
            templateBuildr.append("      data-date-min-view-mode='days'> ");
            templateBuildr.append("    <span class='input-group-addon' style='width: 0; padding: 0; border: none;'></span> ");
            templateBuildr.append("    <input wicket:id='time' type='text' class='form-control timepicker'> ");
            templateBuildr.append(" </div> ");
            return templateBuildr.toString();
        });
    }

    protected Map<String, Object> getParams() {
        final Map<String, Object> params = new TreeMap<>();
        params.put("defaultTime", false);
        params.put("showMeridian", false);
        if (dateTimerView != null) {
            params.putAll(dateTimerView.getParams());
        }
        return params;
    }

    private String getJSONParams() {
        final JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : getParams().entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toString();
    }

}
