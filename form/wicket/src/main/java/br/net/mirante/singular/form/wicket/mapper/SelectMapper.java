package br.net.mirante.singular.form.wicket.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MProviderOpcoes;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class SelectMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        final List<String> opcoesValue;
        if (model.getObject().getMTipo() instanceof MTipoString
                && ((MTipoString) model.getObject().getMTipo()).getProviderOpcoes() != null) {
            MProviderOpcoes opcoes = ((MTipoString) model.getObject().getMTipo()).getProviderOpcoes();
            opcoesValue = new ArrayList<>();
            opcoesValue.addAll(opcoes.getOpcoes().getValor()
                    .stream().map(Object::toString).collect(Collectors.toList()));
        } else {
            opcoesValue = Collections.emptyList();
        }

        return formGroupAppender(formGroup, model, opcoesValue);
    }

    protected AbstractSingleSelectChoice<String> retrieveChoices(IModel<? extends MInstancia> model,
            final List<String> opcoesValue) {
        return new DropDownChoice<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), opcoesValue);
    }

    protected Component formGroupAppender(BSControls formGroup, IModel<? extends MInstancia> model,
            final List<String> opcoesValue) {
        final DropDownChoice<String> choices = (DropDownChoice<String>) retrieveChoices(model, opcoesValue);
        formGroup.appendSelect(choices.setNullValid(true));
        return choices;
    }
}
