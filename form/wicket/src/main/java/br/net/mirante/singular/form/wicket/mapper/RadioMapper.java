package br.net.mirante.singular.form.wicket.mapper;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.IValueMap;
import org.apache.wicket.util.value.ValueMap;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class RadioMapper extends SelectMapper {

    @Override
    protected RadioChoice<String> retrieveChoices(IModel<? extends MInstancia> model,
            final List<String> opcoesValue) {
        return new RadioChoice<String>(model.getObject().getNome(), new MInstanciaValorModel<>(model), opcoesValue) {
            @SuppressWarnings("Contract")
            @Override
            protected IValueMap getAdditionalAttributesForLabel(int index, String choice) {
                IValueMap map = new ValueMap();
                map.put("class", "radio-inline");
                map.put("style", "position:relative;top:-1px;padding-left:3px;padding-right:10px;");
                return map;
            }

            @Override
            protected IValueMap getAdditionalAttributes(int index, String choice) {
                IValueMap map = new ValueMap();
                map.put("style", "left:20px;");
                return map;
            }

            @Override
            protected void onConfigure() {
                this.setVisible(!opcoesValue.isEmpty());
            }
        };
    }

    @Override
    protected Component formGroupAppender(BSControls formGroup, IModel<? extends MInstancia> model,
            final List<String> opcoesValue) {
        final RadioChoice<String> choices = retrieveChoices(model, opcoesValue);
        formGroup.appendRadioChoice(choices);
        return choices;
    }
}
