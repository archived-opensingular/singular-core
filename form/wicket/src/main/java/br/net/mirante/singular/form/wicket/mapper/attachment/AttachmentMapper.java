package br.net.mirante.singular.form.wicket.mapper.attachment;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.attachment.MIAttachment;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class AttachmentMapper implements ControlsFieldComponentMapper {

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        AttachmentContainer container = new AttachmentContainer(
                                    (IModel<? extends MIAttachment>) model);
        formGroup.appendTypeahead(container);
        return container.field();
    }

    @Override
    public String getReadOnlyFormatedText(IModel<? extends MInstancia> model) {
        return "";
    }
}
