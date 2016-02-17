package br.net.mirante.singular.form.wicket.mapper.attachment;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.SIAttachment;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSWellBorder;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;

public class AttachmentMapper implements ControlsFieldComponentMapper {

    @Override
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup,
                                 IModel<? extends SInstance> model, IModel<String> labelModel) {
        final FileUploadPanel container = new FileUploadPanel("container", (IModel<SIAttachment>) model);
        formGroup.appendDiv(container);
        return container.getUploadField();
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        return StringUtils.EMPTY;
    }

    @Override
    public Component appendReadOnlyInput(MView view, BSContainer bodyContainer,
                                         BSControls formGroup, IModel<? extends SInstance> model,
                                         IModel<String> labelModel) {

        final TemplatePanel templatePanel = formGroup.newTemplateTag(tt -> {
            String template = "";
            template += "<div wicket:id='outputBorder'>";
            template += "   <a wicket:id='downloadLink'><span wicket:id='fileName'></span></a>";
            template += "</div>";
            return template;
        });

        final BSWellBorder outputBorder = BSWellBorder.small("outputBorder");
        final SIAttachment attachment = (SIAttachment) model.getObject();
        final String fileId = attachment.getFileId();
        final IAttachmentRef attachmentRef;

        if (fileId != null
                && (attachmentRef = getAttachmentRef(attachment.getDocument(), fileId)) != null) {

            final byte[] content = attachmentRef.getContentAsByteArray();
            final String fileName = attachment.getFileName();

            final Link<Void> downloadLink = new Link<Void>("downloadLink") {
                @Override
                public void onClick() {
                    AbstractResourceStreamWriter writer = new AbstractResourceStreamWriter() {
                        @Override
                        public void write(OutputStream outputStream) throws IOException {
                            outputStream.write(content);
                        }
                    };
                    ResourceStreamRequestHandler requestHandler = new ResourceStreamRequestHandler(writer);
                    requestHandler.setFileName(fileName);
                    requestHandler.setContentDisposition(ContentDisposition.ATTACHMENT);
                    final RequestCycle requestCycle = getRequestCycle();
                    requestCycle.scheduleRequestHandlerAfterCurrent(requestHandler);
                }
            };
            final Label fileNameLabel = new Label("fileName", $m.ofValue(fileName));
            outputBorder.add(downloadLink);
            downloadLink.add(fileNameLabel);
        } else {
            outputBorder.add(new WebMarkupContainer("downloadLink") {
                @Override
                public boolean isVisible() {
                    return false;
                }
            });
        }

        templatePanel.add(outputBorder);

        return templatePanel;
    }

    private IAttachmentRef getAttachmentRef(final SDocument document, final String fileId) {
        IAttachmentPersistenceHandler temporaryHandler = document.getAttachmentPersistenceTemporaryHandler();

        if (temporaryHandler.getAttachment(fileId) != null) {
            return temporaryHandler.getAttachment(fileId);
        } else {
            IAttachmentPersistenceHandler persistenceHandler = document.getAttachmentPersistencePermanentHandler();
            return persistenceHandler.getAttachment(fileId);
        }
    }
}
