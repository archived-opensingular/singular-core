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
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.servlet.MimeTypes;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.view.FileEventListener;
import org.opensingular.form.wicket.behavior.DisabledClassBehavior;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.attachment.BaseJQueryFileUploadBehavior;
import org.opensingular.form.wicket.mapper.attachment.DownloadLink;
import org.opensingular.form.wicket.mapper.attachment.DownloadSupportedBehavior;
import org.opensingular.form.wicket.mapper.attachment.image.SIAttachmentIResourceStream;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadConfig;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadManagerFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.SingularUploadException;
import org.opensingular.form.wicket.mapper.attachment.upload.UploadResponseWriter;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy.AttachmentKeyStrategy;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy.ServletFileUploadStrategy.PARAM_NAME;
import static org.opensingular.lib.commons.base.SingularProperties.SINGULAR_FILEUPLOAD_MAXCHUNKSIZE;

public class FileUploadPanel extends Panel implements Loggable {

    public static final String DEFAULT_FILE_UPLOAD_MAX_CHUNK_SIZE = "2000000";

    private final FileUploadManagerFactory upManagerFactory = new FileUploadManagerFactory();
    private final UploadResponseWriter     upResponseWriter = new UploadResponseWriter();

    private       AddFileBehavior adder;
    private final ViewMode        viewMode;

    private final FileUploadPanel    self             = this;
    private final AjaxButton         removeFileButton = new RemoveButton("remove_btn");
    private final WebMarkupContainer uploadFileButton = new UploadButton("upload_btn");

    private FileUploadField    fileField;
    private WebMarkupContainer filesContainer, progressBar, downloadLinkContainer;
    private DownloadSupportedBehavior downloader;
    private DownloadLink              downloadLink;
    private AttachmentKey             uploadId;

    private boolean showPreview = false;

    private WebMarkupContainer preview;

    private AbstractDefaultAjaxBehavior previewCallBack;

    private List<FileEventListener> fileUploadedListeners = new ArrayList<>();
    private List<FileEventListener> fileRemovedListeners  = new ArrayList<>();

    private IConsumer<AjaxRequestTarget> consumerAfterLoadImage; //Behavior that will be executed after load the image.
    private IConsumer<AjaxRequestTarget> consumerAfterRemoveImage; //Behavior that will be executed after remove the image.

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

        downloadLinkContainer = new WebMarkupContainer("input-div");
        downloadLinkContainer.add(new DisabledClassBehavior("singular-upload-field-disabled"));
        downloadLink = new DownloadLink("downloadLink", self.getModel(), downloader);
        filesContainer = new WebMarkupContainer("files");

        progressBar = new WebMarkupContainer("progress");

        add(downloadLinkContainer);
        downloadLinkContainer.add(filesContainer);
        filesContainer.add(downloadLink);
        downloadLinkContainer.add(progressBar);


        fileField = new FileUploadField("fileUpload", dummyModel(self.getModel()));
        fileField.add(new DisabledClassBehavior("singular-upload-disabled"));
        fileField.add(new AttributeAppender("title", "Nenhum arquivo selecionado."));
        add(uploadFileButton.add(fileField));
        add(removeFileButton.add(new AttributeAppender("title", "Excluir")));


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
        preview.setOutputMarkupPlaceholderTag(true);
        Image imagePreview = new Image("imagePreview", new ResourceStreamResource(new SIAttachmentIResourceStream(self.getModel())));
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
                if (consumerAfterLoadImage != null) {
                    consumerAfterLoadImage.accept(target);
                }
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
            this.uploadId = fileUploadManager.createUpload(getMaxFileSizeByProperties(), null, attachment.asAtr().getAllowedFileTypes(), this::getTemporaryHandler);
        }
    }

    /**
     * This method will verify if the maxFileSize of the SType is minor than the Global config,
     * if it's not, than the max value will be the global one.
     *
     * @return Return the min of max file size.
     */
    private long getMaxFileSizeByProperties() {
        return new FileUploadConfig(SingularProperties.get()).resolveMaxPerFile(getMaxFileSize());
    }

    private IAttachmentPersistenceHandler getTemporaryHandler() {
        return getModel().getObject().getDocument().getAttachmentPersistenceTemporaryHandler();
    }

    @Override
    @SuppressWarnings("squid:S2095")
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        PackageTextTemplate fileUploadJSTemplate = new PackageTextTemplate(FileUploadPanel.class, "FileUploadPanel.js");
        fileUploadJSTemplate.setCharset(StandardCharsets.UTF_8);
        fileUploadJSTemplate.setEncoding("UTF-8");
        Map<String, String> params               = new HashMap<>();
        params.put("maxChunkSize", SingularProperties.get(SINGULAR_FILEUPLOAD_MAXCHUNKSIZE, DEFAULT_FILE_UPLOAD_MAX_CHUNK_SIZE));

        response.render(OnDomReadyHeaderItem.forScript(fileUploadJSTemplate.interpolate(params).asString()));
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
                    .put("max_file_size", getMaxFileSizeByProperties())
                    .put("allowed_file_types", JSONObject.wrap(getAllowedFileTypes()))
                    .put("preview_update_callback", previewCallBack.getCallbackUrl())
                    .put("allowed_file_extensions", JSONObject.wrap(getAllowedExtensions()))
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
        return AttachmentKeyStrategy.getUploadUrl(getServletRequest(), uploadId);
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

    private Set<String> getAllowedExtensions() {
        return MimeTypes.getExtensionsFormMimeTypes(getAllowedFileTypes(), true);
    }

    public FileUploadField getUploadField() {
        return fileField;
    }

    public FileUploadPanel registerFileUploadedListener(FileEventListener fileUploadedListener) {
        this.fileUploadedListeners.add(fileUploadedListener);
        return this;
    }

    public FileUploadPanel registerFileRemovedListener(FileEventListener fileRemovedListener) {
        this.fileRemovedListeners.add(fileRemovedListener);
        return this;
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
            add(DisabledClassBehavior.getInstance());
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
            for (FileEventListener fileRemovedListener : fileRemovedListeners) {
                fileRemovedListener.accept(self.getModelObject());
            }

            self.getModelObject().clearInstance();
            if (self.getModelObject().getParent() instanceof SIList) {
                final SIList<?> parent = (SIList<?>) self.getModelObject().getParent();
                parent.remove(parent.indexOf(self.getModelObject()));
                target.add(form);
            } else {
                target.add(FileUploadPanel.this);
            }
            if (consumerAfterRemoveImage != null) {
                consumerAfterRemoveImage.accept(target);
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
                final String pName   = getParamFileId("name").toString();

                getLogger().debug("FileUploadPanel.AddFileBehavior(fileId={},name={})", pFileId, pName);

                Optional<UploadResponseInfo> responseInfo = getFileUploadManager().consumeFile(pFileId, attachment -> {
                    final SIAttachment si = (SIAttachment) FileUploadPanel.this.getDefaultModel().getObject();
                    si.update(attachment);
                    try {
                        for (FileEventListener fileUploadedListener : fileUploadedListeners) {
                            fileUploadedListener.accept(si);
                        }

                        return new UploadResponseInfo(si);
                    } catch (SingularUploadException e) {
                        return new UploadResponseInfo(e.getFileName(), e.getMessage());
                    }
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

    public IConsumer<AjaxRequestTarget> getConsumerAfterLoadImage() {
        return consumerAfterLoadImage;
    }

    public void setConsumerAfterLoadImage(IConsumer<AjaxRequestTarget> consumerAfterLoadImage) {
        this.consumerAfterLoadImage = consumerAfterLoadImage;
    }

    public IConsumer<AjaxRequestTarget> getConsumerAfterRemoveImage() {
        return consumerAfterRemoveImage;
    }

    public void setConsumerAfterRemoveImage(IConsumer<AjaxRequestTarget> consumerAfterRemoveImage) {
        this.consumerAfterRemoveImage = consumerAfterRemoveImage;
    }
}
