package br.net.mirante.singular.form.wicket.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.IValueMap;
import org.apache.wicket.util.value.ValueMap;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MProviderOpcoes;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class RadioMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        final List<String> opcoesValue;
        if (model.getObject().getMTipo() instanceof MTipoString
                && ((MTipoString) model.getObject().getMTipo()).getProviderOpcoes() != null) {
            MProviderOpcoes opcoes = ((MTipoString) model.getObject().getMTipo()).getProviderOpcoes();
            opcoesValue = new ArrayList<>();
            opcoesValue.addAll(opcoes.getOpcoes().getValor()
                    .stream().map(Object::toString).collect(Collectors.toList()));
        } else {
            opcoesValue = Collections.emptyList();
        }

        final RadioChoice<String> choices =
                new RadioChoice<String>(model.getObject().getNome(), new MInstanciaValorModel<>(model), opcoesValue) {
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

        formGroup.appendRadioChoice(choices);
        return choices;
    }
}
