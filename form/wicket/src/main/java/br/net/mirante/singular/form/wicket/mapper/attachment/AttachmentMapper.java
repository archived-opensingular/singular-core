package br.net.mirante.singular.form.wicket.mapper.attachment;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.form.mform.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;


public class AttachmentMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(SView view, BSContainer bodyContainer, BSControls formGroup,
                                 IModel<? extends SInstance> model, IModel<String> labelModel) {
        final FileUploadPanel container = new FileUploadPanel("container", (IModel<SIAttachment>) model, ViewMode.EDITION);
        formGroup.appendDiv(container);
        return container.getUploadField();
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        throw new SingularFormException("Este metodo não deve ser acessado, para download utilizar appendReadOnlyInput");
    }

    @Override
    public Component appendReadOnlyInput(SView view, BSContainer bodyContainer,
                                         BSControls formGroup, IModel<? extends SInstance> model,
                                         IModel<String> labelModel) {
        final FileUploadPanel container = new FileUploadPanel("container", (IModel<SIAttachment>) model, ViewMode.VISUALIZATION);
        formGroup.appendDiv(container);
        return container.getUploadField();
    }

}
