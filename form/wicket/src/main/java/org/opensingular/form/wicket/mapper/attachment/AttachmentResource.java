package org.opensingular.form.wicket.mapper.attachment;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.string.StringValue;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Shared Resource vinculado a uma sessão.
 *
 * @see org.opensingular.form.wicket.mapper.attachment.DownloadSupportedBehavior
 */
public class AttachmentResource extends AbstractResource implements Loggable {

    private Map<String, Attachment> attachments = new HashMap<>();
    private String sessionKey;

    public AttachmentResource(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        ResourceResponse resourceResponse = new ResourceResponse();

        if (!Session.exists() || sessionKey == null) {
            return resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }

        String id = Session.get().getId();

        if (id != null && !id.equals(sessionKey)) {
            return resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }

        StringValue attachmentKey = attributes.getParameters().get("attachmentKey");

        if (attachmentKey.isNull() || attachmentKey.isEmpty()) {
            return resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }

        Attachment attachment = attachments.get(attachmentKey.toString());

        if (attachment == null) {
            return resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }

        IAttachmentRef attachmentRef = attachment.attachmentRef;

        if (attachmentRef.getSize() > 0) {
            resourceResponse.setContentLength(attachmentRef.getSize());
        }

        resourceResponse.setFileName(attachment.filename);

        try {
            resourceResponse.setContentDisposition(attachment.contentDisposition);
            resourceResponse.setContentType(attachmentRef.getContentType());
            resourceResponse.setWriteCallback(new AttachmentResourceWriteCallback(resourceResponse, attachmentRef));
        } catch (Exception e) {
            getLogger().error("Erro ao recuperar arquivo.", e);
            resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }

        return resourceResponse;
    }

    /**
     * @param name        the file name
     * @param disposition the disposition
     * @param ref         the reference
     * @return the URL do download
     */
    public String addAttachment(String name, ContentDisposition disposition, IAttachmentRef ref) {
        WebApplication app = WebApplication.get();
        attachments.put(ref.getId(), new Attachment(name, disposition, ref));
        String path = app.getServletContext().getContextPath() + "/" + app.getWicketFilter().getFilterPath() + getDownloadURL(Session.get().getId(), ref.getId());
        return path.replaceAll("\\*", "").replaceAll("//", "/");
    }

    public static class Attachment implements Serializable {

        final String filename;
        final ContentDisposition contentDisposition;
        final IAttachmentRef attachmentRef;

        public Attachment(String filename, ContentDisposition contentDisposition, IAttachmentRef attachmentRef) {
            this.filename = filename;
            this.contentDisposition = contentDisposition;
            this.attachmentRef = attachmentRef;
        }

    }

    public static String getMountPath(String id) {
        return getDownloadURL(id, "${attachmentKey}");
    }

    public static String getDownloadURL(String id, String path) {
        return "/download/" + id + "/" + path;
    }


}