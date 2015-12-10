package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.IValueMap;
import org.apache.wicket.util.value.ValueMap;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class RadioMapper extends SelectMapper {

    @Override @SuppressWarnings({ "rawtypes", "unchecked" })
    protected RadioChoice retrieveChoices(IModel<? extends MInstancia> model,
            final List<SelectOption<String>> opcoesValue) {
        String id = model.getObject().getNome();
        return new RadioChoice<SelectOption<String>>(id, 
                (IModel)new MSelectionInstanceModel<SelectOption>(model), opcoesValue, rendererer()) {
            @Override
            protected IValueMap getAdditionalAttributesForLabel(int index, SelectOption<String> choice) {
                IValueMap map = new ValueMap();
                map.put("class", "radio-inline");
                map.put("style", "position:relative;top:-1px;padding-left:3px;padding-right:10px;");
                return map;
            }

            @Override
            protected IValueMap getAdditionalAttributes(int index, SelectOption<String> choice) {
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

    @Override @SuppressWarnings("unchecked")
    protected Component formGroupAppender(BSControls formGroup, IModel<? extends MInstancia> model,
            final List<SelectOption<String>> opcoesValue) {
        final RadioChoice<String> choices = retrieveChoices(model, opcoesValue);
        formGroup.appendRadioChoice(choices);
        return choices;
    }
}
