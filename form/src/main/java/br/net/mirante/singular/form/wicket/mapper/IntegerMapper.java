package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.model.instancia.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class IntegerMapper implements ControlsFieldComponentMapper {
    @Override
    public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        TextField<Integer> comp = new TextField<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), Integer.class);
        formGroup.appendInputText(comp.setLabel(labelModel));
        return comp;
    }
}