package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class PicklistMapper extends MultipleSelectMapper {

    @Override @SuppressWarnings("rawtypes")
    protected Component formGroupAppender(BSControls formGroup, 
            IModel<? extends SInstance2> model,
            final List<SelectOption> opcoesValue) {
        return formGroup.appendPicklist(retrieveChoices(model, opcoesValue));
    }
}