package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.form.wicket.behavior.TelefoneNacionalMaskBehaviour;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;


public class TelefoneNacionalMapper extends StringMapper {

    @Override
    public Component appendInput(SView view, BSContainer bodyContainer, BSControls formGroup,
                                 IModel<? extends SInstance> model, IModel<String> labelModel) {
        final Component inputComponent = super.appendInput(view, bodyContainer, formGroup, model, labelModel);
        inputComponent.add(new TelefoneNacionalMaskBehaviour());
        return inputComponent;
    }
}
