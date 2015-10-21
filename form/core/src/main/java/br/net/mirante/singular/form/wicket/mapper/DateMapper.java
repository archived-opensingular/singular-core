package br.net.mirante.singular.form.wicket.mapper;

import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

import static br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior.Masks;

public class DateMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        TextField<?> comp = new TextField<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), Date.class);
        formGroup.appendDatepicker(comp.setLabel(labelModel)
                .setOutputMarkupId(true).add(new InputMaskBehavior(Masks.FULL_DATE)));
        return comp;
    }
}
