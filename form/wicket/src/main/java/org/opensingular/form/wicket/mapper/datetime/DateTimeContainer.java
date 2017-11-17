/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.mapper.datetime;

import org.opensingular.form.view.SViewDateTime;
import org.opensingular.form.wicket.behavior.InputMaskBehavior;
import org.opensingular.form.wicket.behavior.InputMaskBehavior.Masks;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.form.wicket.model.SIDateTimeModel;
import org.opensingular.lib.wicket.util.behavior.DatePickerInitBehaviour;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
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
        params.put("defaultTime", Boolean.FALSE);
        params.put("showMeridian", Boolean.FALSE);
        if (dateTimerView != null) {
            params.put("showMeridian", dateTimerView.isMode24hs());
            params.put("minuteStep", dateTimerView.getMinuteStep());
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
