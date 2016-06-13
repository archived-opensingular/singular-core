package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

import static br.net.mirante.singular.form.wicket.mapper.attachment.FileUploadPanel.PARAM_NAME;
import static java.lang.Integer.*;
import static org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem.*;

/**
 * Created by nuk on 10/06/16.
 */
public class FileListUploadPanel extends Panel {

    private final FileUploadField fileField;
    private final WebMarkupContainer fileList;
    private final DownloadBehavior downloader;
    private final AddFileBehavior adder;

    public FileListUploadPanel(String id, IModel<SIList<SIAttachment>> model) {
        super(id, model);
        add(fileField = new FileUploadField("fileUpload", dummyModel()));
        add(fileList = new WebMarkupContainer("fileList"));
        add(downloader = new DownloadBehavior(model.getObject().getDocument()
                .getAttachmentPersistenceTemporaryHandler()));
        add(adder = new AddFileBehavior());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(forReference(resourceRef("FileListUploadPanel.js")));
        response.render(OnDomReadyHeaderItem.forScript(generateInitJS()));
    }

    private String generateInitJS() {
        return " $(function () { \n" +
                "     var params = { \n" +
                "             file_field_id: '"+fileField.getMarkupId()+"', \n" +
                "             fileList_id: '"+fileList.getMarkupId()+"', \n" +
//                "             files_id : '"+ filesContainer.getMarkupId()+"', \n" +
//                "             progress_bar_id : '"+progressBar.getMarkupId()+"', \n" +
                "  \n" +
//                "             name_id: '"+nameField.getMarkupId()+"', \n" +
//                "             id_id: '"+idField.getMarkupId()+"', \n" +
//                "             hash_id: '"+hashField.getMarkupId()+"', \n" +
//                "             size_id: '"+sizeField.getMarkupId()+"', \n" +
//                "  \n" +
                "             param_name : '"+PARAM_NAME+"', \n" +
                "             upload_url : '"+ uploadUrl() +"', \n" +
                "             upload_id : '"+ serviceId().toString()+"', \n" +
                "             download_url : '"+downloader.getUrl()+"', \n" +
                "             add_url : '"+adder.getUrl()+"', \n" +
                "  \n" +
                "     }; \n" +
                "  \n" +
                "     window.FileListUploadPanel.setup(params); \n" +
                " });";
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }

    private String uploadUrl() {
        String contextPath = getWebApplication().getServletContext().getContextPath();
        return contextPath + FileUploadServlet.UPLOAD_URL;
    }

    private UUID serviceId() {
        IAttachmentPersistenceHandler service = ((SIList) getDefaultModel().getObject()).getDocument().getAttachmentPersistenceTemporaryHandler();
        HttpSession session = ((ServletWebRequest) getRequest()).getContainerRequest().getSession();

        return FileUploadServlet.registerService(session, service);
    }

    private IMInstanciaAwareModel dummyModel() {
        return new IMInstanciaAwareModel() {
            @Override
            public Object getObject() {return null;}

            @Override
            public void setObject(Object object) {}

            @Override
            public void detach() {}

            @Override
            public SInstance getMInstancia() {
                return (SInstance) getDefaultModel().getObject();
            }
        };
    }

    private class AddFileBehavior extends Behavior implements IResourceListener {
        transient protected WebWrapper w = new WebWrapper();
        private Component component;

        @Override
        public void onResourceRequested() {
            try {
                ServletWebRequest request = w.request();
                IRequestParameters parameters = request.getRequestParameters();
                StringValue id = parameters.getParameterValue("fileId");
                StringValue name = parameters.getParameterValue("name");
                StringValue hashSHA1 = parameters.getParameterValue("hashSHA1");
                StringValue size = parameters.getParameterValue("size");
                IModel<?> model = FileListUploadPanel.this.getDefaultModel();
                SIList<SIAttachment> list = (SIList<SIAttachment>) model.getObject();
                SIAttachment siAttachment = list.addNew();
                siAttachment.setFileId(id.toString());
                siAttachment.setFileName(name.toString());
                siAttachment.setFileHashSHA1(hashSHA1.toString());
                siAttachment.setFileSize(parseInt(size.toString()));
            } catch (Exception e) {
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        @Override
        public void bind(Component component) {
            this.component = component;
        }

        public String getUrl() {
            return component.urlFor(this, IResourceListener.INTERFACE, new PageParameters()).toString();
        }
    }

}
