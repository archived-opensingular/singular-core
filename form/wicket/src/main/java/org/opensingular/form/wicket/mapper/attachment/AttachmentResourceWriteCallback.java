package org.opensingular.form.wicket.mapper.attachment;

import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class AttachmentResourceWriteCallback extends AbstractResource.WriteCallback implements Loggable {

    private final AbstractResource.ResourceResponse resourceResponse;
    private final IAttachmentRef fileRef;

    public AttachmentResourceWriteCallback(AbstractResource.ResourceResponse resourceResponse, IAttachmentRef fileRef) {
        this.resourceResponse = resourceResponse;
        this.fileRef = fileRef;
    }

    @Override
    public void writeData(IResource.Attributes attributes) throws IOException {
        try (InputStream inputStream = fileRef.getContentAsInputStream()) {
            writeStream(attributes, inputStream);
        } catch (Exception e) {
            getLogger().error("Erro ao recuperar arquivo.", e);
            ((WebResponse) attributes.getResponse()).setStatus(HttpServletResponse.SC_NOT_FOUND);
            resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
