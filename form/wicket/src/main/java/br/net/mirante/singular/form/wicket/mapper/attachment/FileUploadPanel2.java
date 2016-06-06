package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.panel.SUploadProgressBar;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSWellBorder;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.time.Duration;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

/**
 * Created by nuk on 27/05/16.
 */
public class FileUploadPanel2 extends Panel {
    public static String PARAM_NAME = "FILE-UPLOAD";

    private final IModel<SIAttachment> model;
    private final ViewMode viewMode;

    private final FileUploadField fileField;
    private final HiddenField nameField, hashField, sizeField, idField;
    private final WebMarkupContainer filesContainer, progressBar;
    private final DownloadBehavior downloader;
    private final UploadBehavior uploader;

    private final Label fileName = new Label("fileName", new AbstractReadOnlyModel<String>() {
        @Override
        public String getObject() {
            if (!model.getObject().isEmptyOfData()) {
                return model.getObject().getFileName();
            }
            return StringUtils.EMPTY;
        }
    }) {
        @Override
        protected void onConfigure() {
            super.onConfigure();
            add($b.attr("title", $m.ofValue(model.getObject().getFileName())));
        }
    };

    private final Link<Void> downloadLink = new Link<Void>("downloadLink") {

        private static final String SELF = "_self", BLANK = "_blank";
        private IModel<String> target = $m.ofValue(SELF);

        @Override
        public void onClick() {
            final AbstractResourceStreamWriter writer = new AbstractResourceStreamWriter() {
                @Override
                public void write(OutputStream outputStream) throws IOException {
                    outputStream.write(model.getObject().getContentAsByteArray());
                }
            };

            final ResourceStreamRequestHandler requestHandler = new ResourceStreamRequestHandler(writer);

            requestHandler.setFileName(model.getObject().getFileName());
            requestHandler.setCacheDuration(Duration.NONE);

            if (model.getObject().isContentTypeBrowserFriendly()) {
                requestHandler.setContentDisposition(ContentDisposition.INLINE);
            } else {
                requestHandler.setContentDisposition(ContentDisposition.ATTACHMENT);
            }

            getRequestCycle().scheduleRequestHandlerAfterCurrent(requestHandler);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();
            add(new AttributeModifier("target", target));
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
            if (model.getObject().isContentTypeBrowserFriendly()) {
                target.setObject(BLANK);
            } else {
                target.setObject(SELF);
            }
        }
    };

    private final AjaxButton removeFileButton = new AjaxButton("remove_btn") {

        @Override
        protected void onInitialize() {
            super.onInitialize();
            add(new ClassAttributeModifier() {
                protected Set<String> update(Set<String> oldClasses) {
                    if(model.getObject().getFileId() == null){
                        oldClasses.add("file-trash-button-hidden");
                    }
                    return oldClasses;
                }
            });
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onSubmit(target, form);
            model.getObject().clearInstance();
            if (model.getObject().getParent() instanceof SIList) {
                final SIList parent = (SIList) model.getObject().getParent();
                parent.remove(parent.indexOf(model.getObject()));
                target.add(form);
            } else {
                target.add(FileUploadPanel2.this);
            }
        }

    };

    private final WebMarkupContainer uploadFileButton = new WebMarkupContainer("upload_btn") {

        @Override
        protected void onInitialize() {
            super.onInitialize();
            add(new ClassAttributeModifier() {
                protected Set<String> update(Set<String> oldClasses) {
                    if(model.getObject().getFileId() != null){
                        oldClasses.add("file-trash-button-hidden");
                    }
                    return oldClasses;
                }
            });
        }
    };

    public FileUploadPanel2(String id, IModel<SIAttachment> model, ViewMode viewMode) {
        super(id, model);
        this.model = model;
        this.viewMode = viewMode;

        fileField = new FileUploadField("fileUpload", new IMInstanciaAwareModel() {
            @Override
            public Object getObject() {return null;}

            @Override
            public void setObject(Object object) {}

            @Override
            public void detach() {}

            @Override
            public SInstance getMInstancia() {
                return model.getObject();
            }
        });
        nameField = new HiddenField("file_name",
                new PropertyModel<>(model, "fileName"));
        hashField = new HiddenField("file_hash",
                new PropertyModel<>(model, "fileHashSHA1"));
        sizeField = new HiddenField("file_size",
                new PropertyModel<>(model, "fileSize"));
        idField = new HiddenField("file_id",
                new PropertyModel<>(model, "fileId"));

        add(    (filesContainer = new WebMarkupContainer("files"))
                    .add(downloadLink.add(fileName)),
                uploadFileButton.add(fileField),
                removeFileButton,
                nameField, hashField, sizeField, idField,
                progressBar = new WebMarkupContainer("progress")
        );
        add(
            downloader = new DownloadBehavior(model.getObject()),
            uploader = new UploadBehavior(model.getObject())
        );
    }

    private WebMarkupContainer buildFileDummyField(String id) {
        WebMarkupContainer markup;
        if (viewMode.isEdition()) {
            markup = new WebMarkupContainer(id);
            markup.add($b.classAppender("form-control"));
            markup.add($b.classAppender("fileDummyField"));
            return markup;
        } else {
            markup = BSWellBorder.small(id);
        }
        return markup;
    }

    public WebMarkupContainer buildAttachmentShadow() {
        WebMarkupContainer attachmentShadow = new WebMarkupContainer("attachmentShadow");
        if (viewMode.isEdition()) {
            attachmentShadow.add($b.classAppender("attachmentShadow"));
        } else {
            attachmentShadow.add($b.attr("style", "display:none;"));
        }
        return attachmentShadow;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("FileUploadPanel2.js")));
        response.render(OnDomReadyHeaderItem.forScript(
                " $(function () { \n" +
                "     var params = { \n" +
                "             file_field_id: '"+fileField.getMarkupId()+"', \n" +
                "             files_id : '"+ filesContainer.getMarkupId()+"', \n" +
                "             progress_bar_id : '"+progressBar.getMarkupId()+"', \n" +
                "  \n" +
                "             name_id: '"+nameField.getMarkupId()+"', \n" +
                "             id_id: '"+idField.getMarkupId()+"', \n" +
                "             hash_id: '"+hashField.getMarkupId()+"', \n" +
                "             size_id: '"+sizeField.getMarkupId()+"', \n" +
                "  \n" +
                "             param_name : '"+PARAM_NAME+"', \n" +
                "             upload_url : '"+uploader.getUrl()+"', \n" +
                "             download_url : '"+downloader.getUrl()+"', \n" +
                "  \n" +
                "     }; \n" +
                "  \n" +
                "     window.FileUploadPanel.setup(params); \n" +
                " });"
        ));
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }

    public FileUploadField getUploadField() {
        return fileField;
    }
}
