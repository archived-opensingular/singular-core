package org.opensingular.form.wicket.mapper.attachment;

import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.http.HttpServletResponse;

public class AttachmentResource extends AbstractResource implements Loggable {
    private final String filename;
    private final ContentDisposition contentDisposition;
    private final IAttachmentRef attachmentRef;

    private transient AttachmentShareHandler attachmentSharedHandler;

    public AttachmentResource(String filename,
                              ContentDisposition contentDisposition,
                              IAttachmentRef attachmentRef) {
        this.filename = filename;
        this.contentDisposition = contentDisposition;
        this.attachmentRef = attachmentRef;
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        ResourceResponse resourceResponse = new ResourceResponse();
        if (attachmentRef == null) {
            return resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }
        if (attachmentRef.getSize() > 0) {
            resourceResponse.setContentLength(attachmentRef.getSize());
        }
        resourceResponse.setFileName(filename);
        try {
            resourceResponse.setContentDisposition(contentDisposition);
            resourceResponse.setContentType(attachmentRef.getContentType());
            resourceResponse.setWriteCallback(new AttachmentResourceWriteCallback(resourceResponse, attachmentRef, attachmentSharedHandler));
        } catch (Exception e) {
            getLogger().error("Erro ao recuperar arquivo.", e);
            resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }
        return resourceResponse;
    }

    public void setAttachmentSharedHandler(AttachmentShareHandler attachmentSharedHandler) {
        this.attachmentSharedHandler = attachmentSharedHandler;
    }

}