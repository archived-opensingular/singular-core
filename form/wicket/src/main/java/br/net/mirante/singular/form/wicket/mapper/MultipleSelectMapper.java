package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleSelectMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(MView view, BSContainer bodyContainer, 
            BSControls formGroup, final IModel<? extends MInstancia> model, 
            IModel<String> labelModel) {
        final List<String> opcoesValue;
        final MTipoLista tipoLista;
        if (model.getObject().getMTipo() instanceof MTipoLista) {
            tipoLista = (MTipoLista) model.getObject().getMTipo();
        } else {
            tipoLista = null;
        }
        if ((tipoLista != null) && (tipoLista.getTipoElementos() instanceof MTipoString)
                && (((MTipoString) tipoLista.getTipoElementos()).getProviderOpcoes() != null)) {
            MOptionsProvider opcoes = ((MTipoString) tipoLista.getTipoElementos()).getProviderOpcoes();
            opcoesValue = new ArrayList<>();
            opcoesValue.addAll(opcoes.getOpcoes(model.getObject()).getValor()
                    .stream().map(Object::toString).collect(Collectors.toList()));
        } else {
            opcoesValue = Collections.emptyList();
        }

        return formGroupAppender(formGroup, model, opcoesValue);
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends MInstancia> model) {

        final StringBuilder output = new StringBuilder();
        final MInstancia mi = model.getObject();

        if ((mi != null) && (mi.getValor() != null)
                && (mi.getValor() instanceof List)) {
            List<?> collection = (List<?>) mi.getValor();
            for (Object o : collection) {
                if (collection.indexOf(o) == 0) {
                    output.append(o.toString());
                } else {
                    output.append(", ");
                    output.append(o.toString());
                }
            }
        }

        return output.toString();
    }

    protected ListMultipleChoice<String> retrieveChoices(IModel<? extends MInstancia> model,
            final List<String> opcoesValue) {
        return new ListMultipleChoice<>(model.getObject().getNome(), new MInstanciaValorModel<>(model), opcoesValue);
    }

    protected Component formGroupAppender(BSControls formGroup, IModel<? extends MInstancia> model,
            final List<String> opcoesValue) {
        final ListMultipleChoice<String> choices = retrieveChoices(model, opcoesValue);
        formGroup.appendSelect(choices.setMaxRows(5), true, false);
        return choices;
    }
}
