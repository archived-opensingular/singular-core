package br.net.mirante.singular.form.wicket.mapper;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class MultipleCheckMapper extends MultipleSelectMapper {

    @Override
    protected CheckBoxMultipleChoice<String> retrieveChoices(IModel<? extends MInstancia> model,
            List<String> opcoesValue) {
        return new CheckBoxMultipleChoice<>(model.getObject().getNome(),
                new MInstanciaValorModel<>(model), opcoesValue)
                .setLabelPosition(AbstractChoice.LabelPosition.WRAP_AFTER);
    }

    @Override
    protected Component formGroupAppender(BSControls formGroup, IModel<? extends MInstancia> model,
            final List<String> opcoesValue) {
        final CheckBoxMultipleChoice<String> choices = retrieveChoices(model, opcoesValue);
        formGroup.appendCheckboxChoice(choices);
        return choices;
    }
}
