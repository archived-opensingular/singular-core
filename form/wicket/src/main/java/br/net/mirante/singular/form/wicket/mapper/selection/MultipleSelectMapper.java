package br.net.mirante.singular.form.wicket.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.mapper.selection.MSelectionInstanceModel;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectOption;
import br.net.mirante.singular.form.wicket.mapper.selection.WicketSelectionUtils;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;

import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class MultipleSelectMapper implements ControlsFieldComponentMapper {

    @Override
    @SuppressWarnings("rawtypes")
    public Component appendInput(MView view, BSContainer bodyContainer, 
            BSControls formGroup, final IModel<? extends MInstancia> model, 
            IModel<String> labelModel) {
        final List<SelectOption> opcoesValue;
        final MTipoLista tipoLista;
        if (model.getObject().getMTipo() instanceof MTipoLista) {
            tipoLista = (MTipoLista) model.getObject().getMTipo();
        } else {
            tipoLista = null;
        }
        MTipo elementType = tipoLista.getTipoElementos();
        opcoesValue = WicketSelectionUtils.createOptions(model, elementType);

        return formGroupAppender(formGroup, model, opcoesValue);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected ListMultipleChoice<SelectOption> retrieveChoices(
                                    IModel<? extends MInstancia> model,
                                  final List<SelectOption> opcoesValue) {
        return new ListMultipleChoice<>(model.getObject().getNome(), 
            (IModel)new MSelectionInstanceModel<List<SelectOption>>(model), opcoesValue, renderer());
    }

    @SuppressWarnings("rawtypes")
    protected Component formGroupAppender(BSControls formGroup, 
                                        IModel<? extends MInstancia> model,
                                        final List<SelectOption> opcoesValue) {
        final ListMultipleChoice<SelectOption> choices = retrieveChoices(model, opcoesValue);
        formGroup.appendSelect(choices.setMaxRows(5), true, false);
        return choices;
    }
    
    @SuppressWarnings("rawtypes")
    protected ChoiceRenderer renderer() {
        return new ChoiceRenderer("value", "key");
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

}
