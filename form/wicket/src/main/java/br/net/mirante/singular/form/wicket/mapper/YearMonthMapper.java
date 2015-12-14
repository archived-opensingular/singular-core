package br.net.mirante.singular.form.wicket.mapper;

import java.util.HashMap;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.form.YearMonthField;

@SuppressWarnings("serial")
public class YearMonthMapper implements ControlsFieldComponentMapper {

    @Override @SuppressWarnings("rawtypes")
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        YearMonthField comp = new YearMonthField(model.getObject().getNome(), new MInstanciaValorModel<>(model));
        formGroup.appendDatepicker(comp.setLabel(labelModel)
                .setOutputMarkupId(true).add(new InputMaskBehavior(InputMaskBehavior.Masks.SHORT_DATE)),
                new HashMap<String, String>() {{
            put("data-date-format", "mm/yyyy");
            put("data-date-start-view", "months");
            put("data-date-min-view-mode", "months");
        }});
        return comp;
    }
}
