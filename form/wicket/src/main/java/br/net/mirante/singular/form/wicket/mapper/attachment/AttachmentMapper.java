package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.MIAttachment;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSWellBorder;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
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

import java.io.IOException;
import java.io.OutputStream;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class AttachmentMapper implements ControlsFieldComponentMapper {

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        AttachmentContainer container = new AttachmentContainer(
                (IModel<? extends MIAttachment>) model);
        formGroup.appendTypeahead(container);
        return container.field();
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends MInstancia> model) {
        return StringUtils.EMPTY;
    }

    @Override
    public Component appendReadOnlyInput(MView view, BSContainer bodyContainer,
                                         BSControls formGroup, IModel<? extends MInstancia> model,
                                         IModel<String> labelModel) {

        final TemplatePanel templatePanel = formGroup.newTemplateTag(tt -> {
            String template = "";
            template += "<div wicket:id='outputBorder'>";
            template += "   <a wicket:id='downloadLink'><span wicket:id='fileName'></span></a>";
            template += "</div>";
            return template;
        });

        final BSWellBorder outputBorder = BSWellBorder.small("outputBorder");
        final MIAttachment attachment = (MIAttachment) model.getObject();
        final String fileId = attachment.getFileId();

        if (fileId != null) {
            final IAttachmentRef attachmentRef = getAttachmentRef(attachment.getDocument(), fileId);

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
        final IAttachmentPersistenceHandler temporaryHandler = document.lookupService(SDocument.FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class);
        final IAttachmentPersistenceHandler persistenceHandler = document.lookupService(SDocument.FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class);

        if (temporaryHandler.getAttachment(fileId) != null) {
            return temporaryHandler.getAttachment(fileId);
        } else {
            return persistenceHandler.getAttachment(fileId);
        }
    }
}
