package org.opensingular.form.wicket.mapper.attachment;

import org.apache.tika.io.IOUtils;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class AttachmentResourceWriteCallback extends AbstractResource.WriteCallback implements Loggable {

    final AbstractResource.ResourceResponse resourceResponse;
    final IAttachmentRef fileRef;
    final String url;
    final SharedResourceReference ref;

    AttachmentResourceWriteCallback(AbstractResource.ResourceResponse resourceResponse,
                                    IAttachmentRef fileRef,
                                    String url, SharedResourceReference ref) {
        this.resourceResponse = resourceResponse;
        this.fileRef = fileRef;
        this.url = url;
        this.ref = ref;
    }

    @Override
    public void writeData(IResource.Attributes attributes) throws IOException {
        try (InputStream inputStream = fileRef.getContentAsInputStream()) {
            IOUtils.copy(inputStream, attributes.getResponse().getOutputStream());
            /*Desregistrando recurso compartilhado*/
            WebApplication.get().unmount(url);
            WebApplication.get().getSharedResources().remove(ref.getKey());
        } catch (Exception e) {
            getLogger().error("Erro ao recuperar arquivo.", e);
            ((WebResponse) attributes.getResponse()).setStatus(HttpServletResponse.SC_NOT_FOUND);
            resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
