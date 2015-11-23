package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class AttachmentMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        AttachmentContainer container = new AttachmentContainer(model);
        formGroup.appendTypeahead(container);
        return container.field();
//    return container;
    }
}
