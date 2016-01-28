package br.net.mirante.singular.form.wicket.mapper.datetime;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;

import br.net.mirante.singular.form.mform.basic.view.MDateTimerView;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.model.MIDateTimeModel;
import br.net.mirante.singular.util.wicket.behavior.DatePickerInitBehaviour;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;

public class DateTimeContainer extends BSContainer<DateTimeContainer> {

    private final IMInstanciaAwareModel<Date> model;
    private final MDateTimerView dateTimerView;

    public DateTimeContainer(String id, IMInstanciaAwareModel<Date> model, MDateTimerView dateTimerView) {
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
        return new TextField<>("date", new MIDateTimeModel.DateModel(model))
                .add(new DatePickerInitBehaviour());
    }

    protected TextField<String> buildTimeField() {
        final TextField<String> time = new TextField<>("time", new MIDateTimeModel.TimeModel(model));
        time.add(new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                final String script = String.format("$('#%s').timepicker(%s)", component.getMarkupId(true), getJSONParams());
                response.render(OnDomReadyHeaderItem.forScript(script));
            }
        });

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
