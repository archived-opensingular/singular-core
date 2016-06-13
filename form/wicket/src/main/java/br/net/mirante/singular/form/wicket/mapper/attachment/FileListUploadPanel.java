package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import com.google.common.collect.ImmutableMap;
import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

import static br.net.mirante.singular.form.wicket.mapper.attachment.FileUploadPanel.PARAM_NAME;
import static com.google.common.collect.Lists.newArrayList;
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
    private final RemoveFileBehavior remover;

    public FileListUploadPanel(String id, IModel<SIList<SIAttachment>> model) {
        super(id, model);
        add(fileField = new FileUploadField("fileUpload", dummyModel()));
        add(fileList = new WebMarkupContainer("fileList")
        );
        List<ImmutableMap<String, String>> collect = ((SIList<SIAttachment>) model.getObject()).stream()
                .map((f) -> ImmutableMap.of("name", f.getFileName(), "id", f.getFileId()))
                .collect(Collectors.toList());
        fileList.add(
            new ListView("fileItem", collect){
                protected void populateItem(ListItem item) {
                    Map<String, String> file = (Map) item.getModelObject();
                    item.add(new Label("file_name", Model.of(file.get("name"))));
                    item.add( new AjaxButton("remove_btn") {

                        @Override
                        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                            super.onSubmit(target, form);
                            removeFileFrom(model.getObject(), file.get("id"));
                                target.add(FileListUploadPanel.this);
                        }

                    });
                }
            }
        );
        add(downloader = new DownloadBehavior(model.getObject().getDocument()
                .getAttachmentPersistenceTemporaryHandler()));
        add(adder = new AddFileBehavior());
        add(remover = new RemoveFileBehavior());
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
                "             remove_url : '"+remover.getUrl()+"', \n" +
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

    private static void removeFileFrom(SIList<SIAttachment> list, String fileId) {
        SIAttachment file = findFileByID(list, fileId);
        if(file != null){   list.remove(file);  }
    }

    private static SIAttachment findFileByID(SIList<SIAttachment> list, String fileId) {
        SIAttachment file = null;
        for(SIAttachment a : list){
            if(fileId.equals(a.getFileId())){
                file = a;
                break;
            }
        }
        return file;
    }

    private class AddFileBehavior extends Behavior implements IResourceListener {
        transient protected WebWrapper w = new WebWrapper();
        private Component component;

        @Override
        public void onResourceRequested() {
            try {
                populateFromParams(currentInstance().addNew());
            } catch (Exception e) {
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        private SIList<SIAttachment> currentInstance() {
            IModel<?> model = FileListUploadPanel.this.getDefaultModel();
            return (SIList<SIAttachment>) model.getObject();
        }

        private void populateFromParams(SIAttachment siAttachment) {
            siAttachment.setFileId(getParamFileId("fileId").toString());
            siAttachment.setFileName(getParamFileId("name").toString());
            siAttachment.setFileHashSHA1(getParamFileId("hashSHA1").toString());
            siAttachment.setFileSize(parseInt(getParamFileId("size").toString()));
        }

        private StringValue getParamFileId(String fileId) {
            return params().getParameterValue(fileId);
        }

        private IRequestParameters params() {
            ServletWebRequest request = w.request();
            return request.getRequestParameters();
        }

        @Override
        public void bind(Component component) {
            this.component = component;
        }

        public String getUrl() {
            return component.urlFor(this, IResourceListener.INTERFACE, new PageParameters()).toString();
        }
    }

    private class RemoveFileBehavior extends Behavior implements IResourceListener {
        transient protected WebWrapper w = new WebWrapper();
        private Component component;

        @Override
        public void onResourceRequested() {
            try {
                String fileId = getParamFileId("fileId").toString();
                removeFileFrom(currentInstance(), fileId);
            } catch (Exception e) {
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        private SIList<SIAttachment> currentInstance() {
            IModel<?> model = FileListUploadPanel.this.getDefaultModel();
            return (SIList<SIAttachment>) model.getObject();
        }

        private StringValue getParamFileId(String fileId) {
            return params().getParameterValue(fileId);
        }

        private IRequestParameters params() {
            ServletWebRequest request = w.request();
            return request.getRequestParameters();
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
