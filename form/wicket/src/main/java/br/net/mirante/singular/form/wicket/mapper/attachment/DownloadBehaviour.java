package br.net.mirante.singular.form.wicket.mapper.attachment;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.SIAttachment;
import br.net.mirante.singular.form.mform.document.SDocument;

@SuppressWarnings("serial")
public class DownloadBehaviour extends Behavior implements IResourceListener {
    transient protected WebWrapper w = new WebWrapper();
    private Component component;
    transient private SInstance instance;

    public DownloadBehaviour(SInstance instance) {
        this.instance = instance;
    }

    public void setWebWrapper(WebWrapper w) {
        this.w = w;
    }

    @Override
    public void bind(Component component) {
        this.component = component;
    }

    @Override
    public void onResourceRequested() {
        try {
            handleRequest((SIAttachment) instance, instance.getDocument());
        } catch (IOException e) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private void handleRequest(SIAttachment attachment, SDocument document) throws IOException {
        ServletWebRequest request = w.request();
        IRequestParameters parameters = request.getRequestParameters();
        StringValue id = parameters.getParameterValue("fileId");
        StringValue name = parameters.getParameterValue("fileName");
        if ((id.isEmpty() || name.isEmpty() )&& !attachment.isTemporary()) {
            writeFileFromPersistent(attachment, document);
        }else if(attachment.isTemporary()){
            writeFileFromTemporary(document, attachment.getFileId(), attachment.getFileName());
        } else {
            writeFileFromTemporary(document, id.toString(), name.toString());
        }
    }

    private void writeFileFromPersistent(SIAttachment attachment, SDocument document) throws IOException {
        IAttachmentPersistenceHandler handler = document.lookupService(SDocument.FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class);
        IAttachmentRef data = handler.getAttachment(attachment.getFileId());
        writeFileToResponse(attachment.getFileName(), data, w.response());
    }

    private void writeFileFromTemporary(SDocument document, String fileId, String fileName) throws IOException {
        IAttachmentPersistenceHandler handler = document.getAttachmentPersistenceHandler(true);
        IAttachmentRef data = handler.getAttachment(fileId);
        writeFileToResponse(fileName, data, w.response());
    }

    private void writeFileToResponse(String fileName, IAttachmentRef data, WebResponse response) throws IOException {
        setHeader(fileName, response);
        response.getOutputStream().write(data.getContentAsByteArray());
    }

    private void setHeader(String fileName, WebResponse response) {
        response.addHeader("Content-Type", "application/octet-stream");
        response.addHeader("Content-disposition", "attachment; filename=\"" + fileName+"\"");
    }

    public String getUrl() {
        return component.urlFor(this, IResourceListener.INTERFACE, new PageParameters()).toString();
    }
}
