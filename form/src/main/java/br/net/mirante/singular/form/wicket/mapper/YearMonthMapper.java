package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.form.YearMonthField;

public class YearMonthMapper implements ControlsFieldComponentMapper {
    @Override
    public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        YearMonthField comp = new YearMonthField(model.getObject().getNome(), new MInstanciaValorModel<>(model));
        formGroup.appendInputText(comp.setLabel(labelModel));
        return comp;
    }
}