package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.model.MIDateTimeModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by nuk on 04/04/16.
 */
public class TimeMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(SView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends SInstance> model, IModel<String> labelModel) {
        final TextField<String> time = new TextField<>("time",
                new MIDateTimeModel.TimeModel(new MInstanciaValorModel<>(model)));
        time.add(new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                final String script = String.format("$('#%s').timepicker(%s)", component.getMarkupId(true), getJSONParams());
                response.render(OnDomReadyHeaderItem.forScript(script));
            }
        });
        time.add(new InputMaskBehavior(InputMaskBehavior.Masks.TIME));
        formGroup.appendInputText(time);
        return time;
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        return null;
    }

    private String getJSONParams() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("timeFormat", "HH:mm");
        jsonObject.put("defaultTime", false);
        jsonObject.put("showMeridian", false);
        return jsonObject.toString();
    }
}
