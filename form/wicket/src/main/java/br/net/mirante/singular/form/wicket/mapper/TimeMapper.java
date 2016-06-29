package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.type.core.STypeTime;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.model.MIDateTimeModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Mapper for data type responsible for storing time (hour and minutes).
 */
public class TimeMapper extends ControlsFieldComponentAbstractMapper {

    @Override
    public Component appendInput() {
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
        final SimpleDateFormat format = new SimpleDateFormat(STypeTime.FORMAT);
        if (model.getObject().getValue() instanceof Date) {
            return format.format(model.getObject().getValue());
        }
        return StringUtils.EMPTY;
    }

    private String getJSONParams() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("defaultTime", false);
        jsonObject.put("showMeridian", false);
        return jsonObject.toString();
    }
}
