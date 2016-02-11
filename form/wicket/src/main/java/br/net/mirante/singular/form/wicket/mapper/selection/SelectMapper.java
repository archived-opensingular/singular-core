package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;

@SuppressWarnings({"rawtypes", "serial"})
public class SelectMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends SInstance> model, IModel<String> labelModel) {

        return formGroupAppender(formGroup, model, getOpcoesValue(view, model), view);
    }

    public IReadOnlyModel<List<SelectOption>> getOpcoesValue(MView view, IModel<? extends SInstance> model) {
        return new IReadOnlyModel<List<SelectOption>>() {
            @Override
            public List<SelectOption> getObject() {
                SType<?> type = model.getObject().getMTipo();
                List<SelectOption> opcoesValue = WicketSelectionUtils.createOptions(model, type);
                return opcoesValue;
            }
        };
    }

    protected Component formGroupAppender(BSControls formGroup, IModel<? extends SInstance> model, final IModel<? extends List<SelectOption>> opcoesValue, MView view) {
        final AbstractSingleSelectChoice<SelectOption> choices = retrieveChoices(model, opcoesValue, view);
        formGroup.appendSelect(choices.setNullValid(true), isMultiple(model), isBSSelect(model));
        return choices;
    }


    protected boolean isBSSelect(IModel<? extends SInstance> model) {
        return false;
    }

    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        if (mi != null && mi.getValor() != null) {
            return mi.getSelectLabel();
        }
        return StringUtils.EMPTY;
    }

    protected boolean isMultiple(IModel<? extends SInstance> model) {
        return false;
    }

    @SuppressWarnings({"unchecked"})
    protected AbstractSingleSelectChoice<SelectOption> retrieveChoices(
            IModel<? extends SInstance> model,
            final IModel<? extends List<SelectOption>> opcoesValue, MView view) {
        String id = model.getObject().getNome();
        return new DropDownChoice<>(id, new MSelectionInstanceModel<>(model), opcoesValue, rendererer());
    }

    protected ChoiceRenderer rendererer() {
        return new ChoiceRenderer("selectLabel", "value");
    }
}
