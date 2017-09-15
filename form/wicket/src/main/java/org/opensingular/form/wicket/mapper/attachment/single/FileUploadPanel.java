/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.mapper.attachment.single;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.attachment.BaseJQueryFileUploadBehavior;
import org.opensingular.form.wicket.mapper.attachment.DownloadLink;
import org.opensingular.form.wicket.mapper.attachment.DownloadSupportedBehavior;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadManagerFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.UploadResponseWriter;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.servlet.FileUploadServlet;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.opensingular.form.wicket.mapper.attachment.upload.servlet.FileUploadServlet.PARAM_NAME;

public class FileUploadPanel extends Panel implements Loggable {

    private final FileUploadManagerFactory upManagerFactory = new FileUploadManagerFactory();
    private final UploadResponseWriter upResponseWriter = new UploadResponseWriter();

    private AddFileBehavior adder;
    private final ViewMode viewMode;

    private final FileUploadPanel self = this;
    private final AjaxButton removeFileButton = new RemoveButton("remove_btn");
    private final WebMarkupContainer uploadFileButton = new UploadButton("upload_btn");

    private FileUploadField fileField;
    private WebMarkupContainer filesContainer, progressBar;
    private DownloadSupportedBehavior downloader;
    private DownloadLink downloadLink;
    private AttachmentKey uploadId;

    private boolean showPreview = false;

    private WebMarkupContainer preview;

    private AbstractDefaultAjaxBehavior previewCallBack;

    public FileUploadPanel(String id, IModel<SIAttachment> model, ViewMode viewMode) {
        super(id, model);
        this.viewMode = viewMode;
        buildFileUploadInput();
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
            @Override
            public List<FileUpload> getObject() {
                return null;
            }

            @Override
            public void setObject(List<FileUpload> object) {
            }

            @Override
            public void detach() {
            }

            @Override
            public SInstance getSInstance() {
                return model.getObject();
            }
            //@formatter:on
        };
    }

    protected void buildFileUploadInput() {


        adder = new AddFileBehavior();
        add(adder);

        downloader = new DownloadSupportedBehavior(self.getModel());
        add(downloader);

        downloadLink = new DownloadLink("downloadLink", self.getModel(), downloader);
        fileField = new FileUploadField("fileUpload", dummyModel(self.getModel()));

        filesContainer = new WebMarkupContainer("files");
        add(filesContainer.add(downloadLink));
        add(uploadFileButton.add(fileField));
        add(removeFileButton.add(new AttributeAppender("title", "Excluir")));

        progressBar = new WebMarkupContainer("progress");
        add(progressBar);

        add(new ClassAttributeModifier() {

            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("fileinput fileinput-new upload-single upload-single-uploaded");
                return oldClasses;
            }
        });
        addPreview();
    }

    private void addPreview() {
        preview = new WebMarkupContainer("preview");
        Image imagePreview = new Image("imagePreview", new DynamicImageResource() {
            @Override
            protected byte[] getImageData(Attributes attributes) {
                return self.getModel().getObject().getContentAsByteArray().orElse(new byte[0]);
            }
        });
        add(preview.add(imagePreview));
        preview.add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);
                component.setVisible(showPreview && !self.getModel().getObject().isEmptyOfData());
            }
        });
        previewCallBack = new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                target.add(preview);
            }
        };
        this.add(previewCallBack);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        final FileUploadManager fileUploadManager = getFileUploadManager();

        if (uploadId == null || !fileUploadManager.findUploadInfoByAttachmentKey(uploadId).isPresent()) {
            final SIAttachment attachment = getModelObject();
            this.uploadId = fileUploadManager.createUpload(attachment.asAtr().getMaxFileSize(), null, attachment.asAtr().getAllowedFileTypes(), this::getTemporaryHandler);
        }
    }

    private IAttachmentPersistenceHandler getTemporaryHandler() {
        return getModel().getObject().getDocument().getAttachmentPersistenceTemporaryHandler();
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
                    + "\n   window.FileUploadPanel.setup(" + new JSONObject()
                    .put("param_name", PARAM_NAME)
                    .put("panel_id", self.getMarkupId())
                    .put("file_field_id", fileField.getMarkupId())
                    .put("files_id", filesContainer.getMarkupId())
                    .put("progress_bar_id", progressBar.getMarkupId())
                    .put("upload_url", getUploadUrl())
                    .put("download_url", getDownloaderUrl())
                    .put("add_url", getAdderUrl())
                    .put("max_file_size", getMaxFileSize())
                    .put("allowed_file_types", getAllowedFileTypes())
                    .put("preview_update_callback", previewCallBack.getCallbackUrl())
                    .toString(2) + "); "
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
        return FileUploadServlet.getUploadUrl(getServletRequest(), uploadId);
    }

    private FileUploadManager getFileUploadManager() {
        return upManagerFactory.getFileUploadManagerFromSessionOrMakeAndAttach(getServletRequest().getSession());
    }

    private HttpServletRequest getServletRequest() {
        return (HttpServletRequest) getWebRequest().getContainerRequest();
    }

    private long getMaxFileSize() {
        return getModelObject().asAtr().getMaxFileSize();
    }

    private List<String> getAllowedFileTypes() {
        return getModelObject().asAtr().getAllowedFileTypes();
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }

    public FileUploadField getUploadField() {
        return fileField;
    }

    private final class UploadButton extends WebMarkupContainer {
        private UploadButton(String id) {
            super(id);
        }

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
    }

    private final class RemoveButton extends AjaxButton {
        private RemoveButton(String id) {
            super(id);
        }

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
    }

    private class AddFileBehavior extends BaseJQueryFileUploadBehavior<SIAttachment> {

        public AddFileBehavior() {
            super(FileUploadPanel.this.getModel());
        }

        @Override
        public void onResourceRequested() {

            final HttpServletResponse httpResp = (HttpServletResponse) getWebResponse().getContainerResponse();

            try {
                final String pFileId = getParamFileId("fileId").toString();
                final String pName = getParamFileId("name").toString();

                getLogger().debug("FileUploadPanel.AddFileBehavior(fileId={},name={})", pFileId, pName);

                Optional<UploadResponseInfo> responseInfo = getFileUploadManager().consumeFile(pFileId, attachment -> {
                    final SIAttachment si = (SIAttachment) FileUploadPanel.this.getDefaultModel().getObject();
                    si.update(attachment);
                    return new UploadResponseInfo(si);
                });

                UploadResponseInfo uploadResponseInfo = responseInfo
                        .orElseThrow(() -> new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND));

                upResponseWriter.writeJsonObjectResponseTo(httpResp, uploadResponseInfo);

            } catch (AbortWithHttpErrorCodeException e) {
                getLogger().error(e.getMessage(), e);
                throw e;

            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }
}
