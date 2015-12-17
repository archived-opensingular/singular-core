package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.IValueMap;
import org.apache.wicket.util.value.ValueMap;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class RadioMapper extends SelectMapper {

    @Override
    @SuppressWarnings("rawtypes")
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        OpcoesModel opcoesModel = new OpcoesModel(model);
        final RadioChoice<?> input = new RadioChoice<Object>(
            model.getObject().getNome(),
            new MInstanciaValorModel<>(model),
            opcoesModel) {
            @SuppressWarnings("Contract")
            @Override
            protected IValueMap getAdditionalAttributesForLabel(int index, Object choice) {
                IValueMap map = new ValueMap();
                map.put("class", "radio-inline");
                map.put("style", "position:relative;top:-1px;padding-left:3px;padding-right:10px;");
                return map;
            }
            @Override
            protected IValueMap getAdditionalAttributes(int index, Object choice) {
                IValueMap map = new ValueMap();
                map.put("style", "left:20px;");
                return map;
            }
            @Override
            protected void onConfigure() {
                this.setVisible(!opcoesModel.getObject().isEmpty());
            }
        };
        formGroup.appendRadioChoice(input);
        return input;
    }
}
