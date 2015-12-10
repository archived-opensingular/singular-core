package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class SelectBSMapper extends SelectMapper {

    @Override
    @SuppressWarnings("rawtypes")
    protected Component formGroupAppender(BSControls formGroup, IModel<? extends MInstancia> model,
            final List<SelectOption<String>> opcoesValue) {
        final DropDownChoice<SelectOption> choices = (DropDownChoice<SelectOption>) retrieveChoices(model, opcoesValue);
        formGroup.appendSelect(choices.setNullValid(true));
        return choices;
    }
}
