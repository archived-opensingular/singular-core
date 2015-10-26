package br.net.mirante.singular.form.wicket.mapper;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class PicklistMapper extends MultipleSelectMapper {

    @Override
    protected Component formGroupAppender(BSControls formGroup, IModel<? extends MInstancia> model,
            final List<String> opcoesValue) {
        final ListMultipleChoice<String> choices = retrieveChoices(model, opcoesValue);
        return formGroup.appendPicklist(choices);
    }
}
