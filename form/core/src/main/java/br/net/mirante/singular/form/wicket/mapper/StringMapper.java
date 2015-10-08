package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class StringMapper implements ControlsFieldComponentMapper {
    @Override
    public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        FormComponent<?> comp;
        if (model.getObject().as(AtrBasic::new).isMultiLinha())
            formGroup
                .appendTextarea(comp = new TextArea<>(model.getObject().getNome(), new MInstanciaValorModel<>(model))
                    .setLabel(labelModel));
        else
            formGroup
                .appendInputText(comp = new TextField<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), String.class)
                    .setLabel(labelModel));
        return comp;
    }
}