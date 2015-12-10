package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class MultipleCheckMapper extends MultipleSelectMapper {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected CheckBoxMultipleChoice<SelectOption<String>> retrieveChoices(
        IModel<? extends MInstancia> model,
            List<SelectOption<String>> opcoesValue) {
        return new CheckBoxMultipleChoice<>(model.getObject().getNome(),
                (IModel)new MSelectionInstanceModel<List<SelectOption>>(model), opcoesValue, renderer())
                .setLabelPosition(AbstractChoice.LabelPosition.WRAP_AFTER);
    }

    @Override
    protected Component formGroupAppender(BSControls formGroup, 
        IModel<? extends MInstancia> model,
            final List<SelectOption<String>> opcoesValue) {
        final CheckBoxMultipleChoice<SelectOption<String>> choices = 
            retrieveChoices(model, opcoesValue);
        formGroup.appendCheckboxChoice(choices);
        return choices;
    }
}
