package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class MultipleSelectBSMapper extends MultipleSelectMapper {

    @Override @SuppressWarnings("rawtypes")
    protected Component formGroupAppender(BSControls formGroup, 
        IModel<? extends SInstance> model,
            final List<SelectOption> opcoesValue) {
        final ListMultipleChoice<SelectOption> choices = 
                                            retrieveChoices(model, opcoesValue);
        formGroup.appendSelect(choices.setMaxRows(5), true);
        return choices;
    }
}
