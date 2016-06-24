package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.mapper.AbstractListaMapper;
import org.apache.wicket.model.IModel;

public class AttachmentListMapper extends AbstractListaMapper {

    final static String MULTIPLE_HIDDEN_UPLOAD_FIELD_ID = "uploadField";
    private final static String CLICK_DELEGATE_SCRIPT_TEMPLATE = "$('#%s').on('click', function(){$('#%s').click();});";

    @Override
    public void buildView(WicketBuildContext ctx) {
        ctx.getContainer().appendTag("div",
                new FileListUploadPanel("up-list",
                        (IModel<SIList<SIAttachment>>) ctx.getModel(), ctx));

    }

}