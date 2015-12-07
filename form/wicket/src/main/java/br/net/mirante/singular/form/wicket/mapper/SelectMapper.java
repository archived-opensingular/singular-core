package br.net.mirante.singular.form.wicket.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.AbstractSingleSelectChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MISelectItem;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class SelectMapper implements ControlsFieldComponentMapper {

    @Override
    @SuppressWarnings("rawtypes")
    public Component appendInput(MView view, BSContainer bodyContainer, 
            BSControls formGroup, IModel<? extends MInstancia> model, 
            IModel<String> labelModel) {
        List<String> opcoesValue = Collections.emptyList();
        MTipo<?> type = model.getObject().getMTipo();
        if (type instanceof MTipoString
                && ((MTipoString) model.getObject().getMTipo()).getProviderOpcoes() != null) {
            opcoesValue = addStringOptions(model);
        } else if(type instanceof MTipoSelectItem){
            MTipoSelectItem selectType = (MTipoSelectItem) type;
            if(selectType.getProviderOpcoes() != null ){
                MOptionsProvider opcoes = selectType.getProviderOpcoes();
                MILista rawOptions = (MILista)opcoes.listAvailableOptions(model.getObject());
                opcoesValue = (List<String>) rawOptions.getValores()
                        .stream()
                        .map((x )-> ((MISelectItem)x).getFieldValue() )
                        .collect(Collectors.toList())
                ;
            }
        }

        return formGroupAppender(formGroup, model, opcoesValue);
    }

    private List<String> addStringOptions(IModel<? extends MInstancia> model) {
        MOptionsProvider opcoes = ((MTipoString) model.getObject().getMTipo()).getProviderOpcoes();
        List<String> opcoesValue = new ArrayList<>();
        opcoesValue.addAll(opcoes.listAvailableOptions(model.getObject()).getValor()
                .stream().map(Object::toString).collect(Collectors.toList()));
        return opcoesValue;
    }

    protected AbstractSingleSelectChoice<String> retrieveChoices(IModel<? extends MInstancia> model,
            final List<String> opcoesValue) {
        return new DropDownChoice<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), opcoesValue);
    }

    protected Component formGroupAppender(BSControls formGroup, IModel<? extends MInstancia> model,
            final List<String> opcoesValue) {
        final DropDownChoice<String> choices = (DropDownChoice<String>) retrieveChoices(model, opcoesValue);
        formGroup.appendSelect(choices.setNullValid(true), false, false);
        return choices;
    }
}
