package org.opensingular.form.wicket.mapper.attachment.single;

import static org.opensingular.form.wicket.mapper.attachment.FileUploadServlet.*;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.resource.PackageResourceReference;

import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.SingularEventsHandlers;
import org.opensingular.form.wicket.mapper.attachment.BaseJQueryFileUploadBehavior;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.singular.commons.util.Loggable;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.wicket.mapper.attachment.DownloadLink;
import org.opensingular.form.wicket.mapper.attachment.DownloadSupportedBehavior;
import org.opensingular.form.wicket.mapper.attachment.DownloadUtil;
import org.opensingular.form.wicket.mapper.attachment.FileUploadServlet;

public class FileUploadPanel extends Panel implements Loggable {

    private FileUploadPanel          self             = this;
    private AddFileBehavior          adder;
    private final ViewMode viewMode;
    private final AjaxButton         removeFileButton = new AjaxButton("remove_btn") {

        @Override
        protected void onInitialize() {
            super.onInitialize();
            add(new ClassAttributeModifier() {
                protected Set<String> update(Set<String> oldClasses) {
                    if (self.getModelObject().getFileId() == null) {
                        oldClasses.add("file-trash-button-hidden");
                    }
                    return oldClasses;
                }
            });
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onSubmit(target, form);
            self.getModelObject().clearInstance();
            if (self.getModelObject().getParent() instanceof SIList) {
                final SIList<?> parent = (SIList<?>) self.getModelObject().getParent();
                parent.remove(parent.indexOf(self.getModelObject()));
                target.add(form);
            } else {
                target.add(FileUploadPanel.this);
            }
        }

    };
    private final WebMarkupContainer uploadFileButton = new WebMarkupContainer("upload_btn") {

        @Override
        protected void onInitialize() {
            super.onInitialize();
            add(new ClassAttributeModifier() {
                protected Set<String> update(Set<String> oldClasses) {
                    if (self.getModelObject().getFileId() != null) {
                        oldClasses.add("file-trash-button-hidden");
                    }
                    return oldClasses;
                }
            });
        }
    };

    private FileUploadField fileField;
    private WebMarkupContainer        filesContainer, progressBar;
    private DownloadSupportedBehavior downloader;
    private DownloadLink              downloadLink;

    public FileUploadPanel(String id, IModel<SIAttachment> model, ViewMode viewMode) {
        super(id, model);
        this.viewMode = viewMode;
    }

    @SuppressWarnings("unchecked")
    public IModel<SIAttachment> getModel() {
        return (IModel<SIAttachment>) getDefaultModel();
    }
    public SIAttachment getModelObject() {
        return (SIAttachment) getDefaultModelObject();
    }

    private ISInstanceAwareModel<List<FileUpload>> dummyModel(final IModel<SIAttachment> model) {
        return new ISInstanceAwareModel<List<FileUpload>>() {
            //@formatter:off
            @Override public List<FileUpload> getObject() { return null; }
            @Override public void setObject(List<FileUpload> object) {}
            @Override public void detach() {}
            @Override public SInstance getMInstancia() { return model.getObject(); }
            //@formatter:on
        };
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(adder = new AddFileBehavior());
        add(downloader = new DownloadSupportedBehavior(self.getModel()));
        downloadLink = new DownloadLink("downloadLink", self.getModel(), downloader);
        fileField = new FileUploadField("fileUpload", dummyModel(self.getModel()));

        add((filesContainer = new WebMarkupContainer("files")).add(downloadLink));
        add(uploadFileButton.add(fileField));
        add(removeFileButton);
        add(progressBar = new WebMarkupContainer("progress"));

        add(new ClassAttributeModifier() {

            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("fileinput fileinput-new upload-single upload-single-uploaded");
                return oldClasses;
            }
        });

        uploadFileButton.add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_MOUSEDOWN_HANDLERS));
        downloadLink.add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_MOUSEDOWN_HANDLERS));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("FileUploadPanel.js")));
        response.render(OnDomReadyHeaderItem.forScript(generateInitJS()));
    }

    private String generateInitJS() {
        if (viewMode.isEdition()) {
            return ""
            //@formatter:off
                + "\n $(function () { "
                + "\n   window.FileUploadPanel.setup({ "
                + "\n     param_name: '"        + PARAM_NAME                    + "',"
                + "\n     panel_id: '"          + self.getMarkupId()            + "',"
                + "\n     file_field_id: '"     + fileField.getMarkupId()       + "',"
                + "\n     files_id: '"          + filesContainer.getMarkupId()  + "',"
                + "\n     progress_bar_id: '"   + progressBar.getMarkupId()     + "',"
                + "\n     upload_url: '"        + getUploadUrl()                + "',"
                + "\n     download_url: '"      + getDownloaderUrl()            + "',"
                + "\n     add_url: '"           + getAdderUrl()                 + "',"
                + "\n     max_file_size: "      + getMaxFileSize()              + "  "
                + "\n   });"
                + "\n });";
            //@formatter:on
        } else {
            return "";
        }
    }

    private String getAdderUrl() {
        return adder.getUrl();
    }

    private String getDownloaderUrl() {
        return downloader.getUrl();
    }

    private String getUploadUrl() {
        String contextPath = getWebApplication().getServletContext().getContextPath();
        return contextPath + FileUploadServlet.UPLOAD_URL;
    }

    private long getMaxFileSize() {
        return getModelObject().asAtr().getMaxFileSize();
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }

    public FileUploadField getUploadField() {
        return fileField;
    }

    private class AddFileBehavior extends BaseJQueryFileUploadBehavior<SIAttachment> {

        public AddFileBehavior() {
            super(FileUploadPanel.this.getModel());
        }

        @Override
        public void onResourceRequested() {
            try {
                SIAttachment siAttachment = (SIAttachment) FileUploadPanel.this.getDefaultModel().getObject();
                siAttachment.setContent(
                    getParamFileId("name").toString(),
                    FileUploadServlet.lookupFile(getParamFileId("fileId").toString()),
                    getParamFileId("size").toLong());
                DownloadUtil.writeJSONtoResponse(siAttachment, RequestCycle.get().getResponse());
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

}
