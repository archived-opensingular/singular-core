package br.net.mirante.singular.form.wicket.mapper;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;

public class SelectMapper implements ControlsFieldComponentMapper {

    @Override
    @SuppressWarnings("rawtypes")
    public Component appendInput(
        MView view,
        BSContainer bodyContainer,
        BSControls formGroup,
        IModel<? extends MInstancia> model,
        IModel<String> labelModel) {

        final DropDownChoice<?> choices = new DropDownChoice<>(
            model.getObject().getNome(),
            new MInstanciaValorModel<>(model),
            new OpcoesModel(model));
        formGroup.appendSelect(choices.setNullValid(true), isMultiple(model), isBSSelect(model));
        return choices;
    }

    protected boolean isBSSelect(IModel<? extends MInstancia> model) {
        return false;
    }

    protected boolean isMultiple(IModel<? extends MInstancia> model) {
        return false;
    }

    protected static final class OpcoesModel implements IReadOnlyModel<List<?>> {
        private final IModel<? extends MInstancia> instanceModel;
        public OpcoesModel(IModel<? extends MInstancia> instanceModel) {
            this.instanceModel = instanceModel;
        }
        @Override
        public List<?> getObject() {
            MInstancia ins = instanceModel.getObject();
            MTipo<?> tipo = ins.getMTipo();
            if (tipo.hasProviderOpcoes()) {
                return tipo.getProviderOpcoes().getOpcoes(ins).getValor();
            }
            return Collections.emptyList();
        }
    }
}
