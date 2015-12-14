package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class SelectMapper implements ControlsFieldComponentMapper {

    @Override
    @SuppressWarnings({ "rawtypes" })
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        MTipo<?> type = model.getObject().getMTipo();
        List<SelectOption> opcoesValue = WicketSelectionUtils.createOptions(model, type);

        return formGroupAppender(formGroup, model, opcoesValue);
    }

    @SuppressWarnings("rawtypes")
    protected Component formGroupAppender(BSControls formGroup, IModel<? extends MInstancia> model, final List<SelectOption> opcoesValue) {
        final DropDownChoice<SelectOption> choices = (DropDownChoice<SelectOption>) retrieveChoices(model, opcoesValue);
        formGroup.appendSelect(choices.setNullValid(true), false, false);
        return choices;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected AbstractSingleSelectChoice<SelectOption> retrieveChoices(
                                    IModel<? extends MInstancia> model, 
                                    final List<SelectOption> opcoesValue) {
        String id = model.getObject().getNome();
        return new DropDownChoice<SelectOption>(id, 
                new MSelectionInstanceModel<SelectOption>(model), opcoesValue, rendererer());
    }

    @SuppressWarnings("rawtypes")
    protected ChoiceRenderer rendererer() {
        return new ChoiceRenderer("value", "key");
    }
}
