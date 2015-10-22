package br.net.mirante.singular.form.wicket.mapper;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MProviderOpcoes;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class RadioMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        List<Object> opcoesValue = null;
        if (model.getObject().getMTipo() instanceof MTipoString) {
            MProviderOpcoes opcoes = ((MTipoString) model.getObject().getMTipo()).getProviderOpcoes();
            if (opcoes != null) {
                opcoesValue = opcoes.getOpcoes().getValor();
            }
        }

        String values = "";
        if (opcoesValue != null) {
            for (Object value : opcoesValue) {
                values = values.concat(value.toString()).concat(" ");
            }
        }
        Label label = new Label(model.getObject().getNome(), values);
        formGroup.appendLabel(label);
        return label;
    }
}
