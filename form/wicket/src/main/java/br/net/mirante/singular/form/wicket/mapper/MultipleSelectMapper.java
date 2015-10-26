package br.net.mirante.singular.form.wicket.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MProviderOpcoes;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class MultipleSelectMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        final List<String> opcoesValue;
        final MTipoLista tipoLista;
        if (model.getObject().getMTipo() instanceof MTipoLista) {
            tipoLista = (MTipoLista) model.getObject().getMTipo();
        } else {
            tipoLista = null;
        }
        if (tipoLista != null && tipoLista.getTipoElementos() instanceof MTipoString
            && ((MTipoString) tipoLista.getTipoElementos()).getProviderOpcoes() != null) {
            MProviderOpcoes opcoes = ((MTipoString) tipoLista.getTipoElementos()).getProviderOpcoes();
            opcoesValue = new ArrayList<>();
            opcoesValue.addAll(opcoes.getOpcoes().getValor()
                    .stream().map(Object::toString).collect(Collectors.toList()));
        } else {
            opcoesValue = Collections.emptyList();
        }

        final ListMultipleChoice<String> choices = new ListMultipleChoice<>(model.getObject().getNome(),
                new MInstanciaValorModel<>(model), opcoesValue);

        formGroup.appendSelect(choices.setMaxRows(5), true);
        return choices;
    }
}
