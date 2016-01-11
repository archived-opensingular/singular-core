package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MBooleanRadioView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;

public class BooleanRadioMapper extends RadioMapper {

    @SuppressWarnings("rawtypes")
    @Override
    public IReadOnlyModel<List<SelectOption>> getOpcoesValue(MView view, IModel<? extends MInstancia> model) {
        MBooleanRadioView booleanRadioView = (MBooleanRadioView) view;
        return new IReadOnlyModel<List<SelectOption>>() {
            @Override
            public List<SelectOption> getObject() {
                List<SelectOption> opcoesValue = new ArrayList<>(2);
                opcoesValue.add(new SelectOption<>(booleanRadioView.labelTrue(), true));
                opcoesValue.add(new SelectOption<>(booleanRadioView.labelFalse(), false));
                return opcoesValue;
            }
        };
    }
}
