package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ListMultipleChoice;

import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class PicklistMapper extends MultipleSelectMapper {

    @Override
    protected Component formGroupAppender(BSControls formGroup, ListMultipleChoice<String> choices) {
        return formGroup.appendPicklist(choices);
    }
}
