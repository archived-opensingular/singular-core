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

package org.opensingular.form.wicket.mapper.attachment.list;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.form.SIList;
import org.opensingular.form.servlet.MimeTypes;
import org.opensingular.form.type.basic.AtrBasic;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewAttachmentList;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.DisabledClassBehavior;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.attachment.BaseJQueryFileUploadBehavior;
import org.opensingular.form.wicket.mapper.attachment.DownloadLink;
import org.opensingular.form.wicket.mapper.attachment.DownloadSupportedBehavior;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadConfig;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadManagerFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.UploadResponseWriter;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.servlet.FileUploadServlet;
import org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy.AttachmentKeyStrategy;
import org.opensingular.form.wicket.mapper.behavior.RequiredLabelClassAppender;
import org.opensingular.form.wicket.mapper.components.ConfirmationModal;
import org.opensingular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.form.wicket.util.WicketFormUtils;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.jquery.JQuery;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.opensingular.form.wicket.mapper.attachment.single.FileUploadPanel.DEFAULT_FILE_UPLOAD_MAX_CHUNK_SIZE;
import static org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy.AttachmentKeyStrategy.PARAM_NAME;
import static org.opensingular.lib.commons.base.SingularProperties.SINGULAR_FILEUPLOAD_MAXCHUNKSIZE;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

/**
 * Lista  os uploads múltiplos.
 * <p>
 * O upload múltiplo executado via jquery para a servlet {@link FileUploadServlet} atualiza
 * o código no cliente via javascript por meio do código java script associado a esse painel FileUploadListPanel.js
 * Para manter os models atualizados o js cliente se comunica com esse panel através do {@link FileUploadListPanel.AddFileBehavior}
 * para remover os arquivos e atualizar os models o js cliente se comunica com esse panel através do {@link FileUploadListPanel.RemoveFileBehavior}
 *
 * @author fabricio, vinicius
 */
public class FileUploadListPanel extends Panel implements Loggable {

    private final FileUploadManagerFactory upManagerFactory = new FileUploadManagerFactory();
    private final UploadResponseWriter     upResponseWriter = new UploadResponseWriter();

    private final Component                 fileField;
    private final WebMarkupContainer        fileList;
    private final AddFileBehavior           adder;
    private final RemoveFileBehavior        remover;
    private final DownloadSupportedBehavior downloader;
    private final WicketBuildContext        ctx;

    private AttachmentKey uploadId;

    @SuppressWarnings("unchecked")
    public FileUploadListPanel(String id, IModel<SIList<SIAttachment>> model, WicketBuildContext ctx,
                               IConsumer<BSContainer<?>> sInstanceActionsContainerConfigurer) {
        super(id, model);
        this.ctx = ctx;

        adder = new AddFileBehavior();
        remover = new RemoveFileBehavior(model);
        downloader = new DownloadSupportedBehavior(model);

        Label label = new Label("uploadLabel", $m.get(() -> ctx.getCurrentInstance().asAtr().getLabel()));
        label.setEscapeModelStrings(!ctx.getCurrentInstance().asAtr().isEnabledHTMLInLabel());
        label.add($b.visibleIfModelObject(StringUtils::isNotEmpty));

        BSContainer<?> sInstanceActionsContainer = new BSContainer<>("sInstanceActionsContainer");
        add(sInstanceActionsContainer);
        sInstanceActionsContainerConfigurer.accept(sInstanceActionsContainer);


        Label subtitle = new Label("uploadSubtitle", $m.get(() -> ctx.getCurrentInstance().asAtr().getSubtitle()));
        label.add($b.visibleIfModelObject(StringUtils::isNotEmpty));


        ViewMode viewMode = ctx.getViewMode();

        if (isEdition(viewMode)) {
            label.add(new RequiredLabelClassAppender(model));
        }

        add(label);
        add(subtitle);

        fileList = new WebMarkupContainer("fileList");
        add(fileList.add(new FilesListView("fileItem", model, ctx)));

        fileField = new WebMarkupContainer("fileUpload");
        add(new WebMarkupContainer("button-container")
                .add(fileField)
                .add(new LabelWithIcon("fileUploadLabel", Model.of(""), DefaultIcons.PLUS, Model.of(fileField.getMarkupId())))
                .add($b.visibleIf(() -> isEdition(viewMode))));

        add(ctx.createFeedbackCompactPanel("feedback"));
        add(new WebMarkupContainer("empty-box")
                .add(new WebMarkupContainer("select-file-link")
                        .add(new Label("select-file-link-message", $m.ofValue("Selecione o(s) arquivo(s)")))
                        .add($b.visibleIf(() -> isEdition(viewMode)))
                        .add($b.onReadyScript(c -> JQuery.on(c, "click", JQuery.$(fileField).append(".click();")))))
                .add(new Label("empty-message", $m.ofValue("Nenhum arquivo adicionado"))
                        .add($b.visibleIf(() -> !isEdition(viewMode))))
                .add($b.visibleIf(() -> model.getObject().isEmpty())));

        add(adder, remover, downloader);
        add($b.classAppender("FileUploadListPanel"));
        add(new DisabledClassBehavior("FileUploadListPanel_disabled"));

        if (viewMode != null && viewMode.isVisualization() && model.getObject().isEmpty()) {
            add($b.classAppender("FileUploadListPanel_empty"));
        }
    }

    private boolean isEdition(ViewMode viewMode) {
        return viewMode != null && viewMode.isEdition();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        final FileUploadManager fileUploadManager = getFileUploadManager();

        if (uploadId == null || !fileUploadManager.findUploadInfoByAttachmentKey(uploadId).isPresent()) {
            final AtrBasic atrAttachment = getModelObject().getElementsType().asAtr();
            this.uploadId = fileUploadManager.createUpload(atrAttachment.getMaxFileSize(), null, atrAttachment.getAllowedFileTypes(), this::getTemporaryHandler);
        }
    }

    private IAttachmentPersistenceHandler getTemporaryHandler() {
        return ctx.getCurrentInstance().getDocument().getAttachmentPersistenceTemporaryHandler();
    }

    private static void removeFileFrom(SIList<SIAttachment> list, String fileId) {
        SIAttachment file = findFileByID(list, fileId);
        if (file != null)
            list.remove(file);
    }

    private static SIAttachment findFileByID(SIList<SIAttachment> list, String fileId) {
        for (SIAttachment file : list)
            if (file.getFileId().equals(fileId))
                return file;
        return null;
    }

    @Override
    @SuppressWarnings("squid:S2095")
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        PackageTextTemplate fileUploadJSTemplate = new PackageTextTemplate(FileUploadListPanel.class, "FileUploadListPanel.js");
        Map<String, String> params               = new HashMap<>();
        params.put("maxChunkSize", SingularProperties.get(SINGULAR_FILEUPLOAD_MAXCHUNKSIZE, DEFAULT_FILE_UPLOAD_MAX_CHUNK_SIZE));
        SView view = ctx.getView();
        if (view instanceof SViewAttachmentList && ((SViewAttachmentList) view).isShowPageBlockWhileUploading()) {
            params.put("showPageBlock", "true");
        } else {
            params.put("showPageBlock", "false");
        }
        response.render(OnDomReadyHeaderItem.forScript(fileUploadJSTemplate.interpolate(params).asString()));
        response.render(OnDomReadyHeaderItem.forScript(generateInitJS()));
    }

    private String generateInitJS() {
        if (ctx.getViewMode().isEdition()) {
            return ""
                    //@formatter:off
                    + "\n $(function () { "
                    + "\n   window.FileUploadListPanel.setup(" + new JSONObject()
                    .put("param_name", PARAM_NAME)
                    .put("component_id", this.getMarkupId())
                    .put("file_field_id", fileField.getMarkupId())
                    .put("fileList_id", fileList.getMarkupId())
                    .put("upload_url", uploadUrl())
                    .put("download_url", downloader.getUrl())
                    .put("add_url", adder.getUrl())
                    .put("remove_url", remover.getUrl())
                    .put("max_file_size", getMaxFileSize())
                    .put("allowed_file_types", JSONObject.wrap(getAllowedTypes()))
                    .put("allowed_file_extensions", JSONObject.wrap(getAllowedExtensions()))
                    .toString(2) + "); "
                    + "\n });";
            //@formatter:on
        } else {
            return "";
        }
    }

    private Set<String> getAllowedExtensions() {
        return MimeTypes.getExtensionsFormMimeTypes(getAllowedTypes(), true);
    }


    protected List<String> getAllowedTypes() {
        return defaultIfNull(
                getModelObject().getElementsType().asAtr().getAllowedFileTypes(),
                Collections.<String>emptyList());
    }

    private long getMaxFileSize() {
        Long             maxFileSizeStype = getModelObject().getElementsType().asAtr().getMaxFileSize();
        return new FileUploadConfig(SingularProperties.get()).resolveMaxPerFile(maxFileSizeStype);
    }

    @SuppressWarnings("unchecked")
    public IModel<SIList<SIAttachment>> getModel() {
        return (IModel<SIList<SIAttachment>>) getDefaultModel();
    }

    @SuppressWarnings("unchecked")
    public SIList<SIAttachment> getModelObject() {
        return (SIList<SIAttachment>) getDefaultModelObject();
    }

    private String uploadUrl() {
        return AttachmentKeyStrategy.getUploadUrl(getServletRequest(), uploadId);
    }

    private HttpServletRequest getServletRequest() {
        return (HttpServletRequest) getWebRequest().getContainerRequest();
    }

    private FileUploadManager getFileUploadManager() {
        return upManagerFactory.getFileUploadManagerFromSessionOrMakeAndAttach(getServletRequest().getSession());
    }

    public static class LabelWithIcon extends Label {

        private final Icon           icon;
        private final IModel<String> forAttrValue;

        public LabelWithIcon(String id, IModel<?> model, Icon icon, IModel<String> forAttrValue) {
            super(id, model);
            this.icon = icon;
            this.forAttrValue = forAttrValue;
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();
            if (forAttrValue != null) {
                add($b.attr("for", forAttrValue.getObject()));
            }
        }

        @Override
        public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
            replaceComponentTagBody(markupStream, openTag, "<i class='" + icon.getCssClass() + "'></i>\n" + getDefaultModelObjectAsString());
        }

    }

    private class AddFileBehavior extends BaseJQueryFileUploadBehavior<SIList<SIAttachment>> {
        public AddFileBehavior() {
            super(FileUploadListPanel.this.getModel());
        }

        @Override
        public void onResourceRequested() {

            final HttpServletResponse httpResp = (HttpServletResponse) getWebResponse().getContainerResponse();

            try {
                final String pFileId = getParamFileId("fileId").toString();
                final String pName   = getParamFileId("name").toString();

                getLogger().debug("FileUploadListPanel.AddFileBehavior(fileId={},name={})", pFileId, pName);

                Optional<UploadResponseInfo> responseInfo = getFileUploadManager().consumeFile(pFileId, attachment -> {
                    final SIAttachment si = currentInstance().addNew();
                    si.update(attachment);
                    return new UploadResponseInfo(si);
                });

                UploadResponseInfo uploadResponseInfo = responseInfo
                        .orElseThrow(() -> new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND));

                upResponseWriter.writeJsonObjectResponseTo(httpResp, uploadResponseInfo);


            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private class RemoveFileBehavior extends BaseJQueryFileUploadBehavior<SIList<SIAttachment>> {
        public RemoveFileBehavior(IModel<SIList<SIAttachment>> listModel) {
            super(listModel);
        }

        @Override
        public void onResourceRequested() {
            try {
                String fileId = getParamFileId("fileId").toString();
                removeFileFrom(currentInstance(), fileId);
                RequestCycle.get().getResponse().write("{\"done\": true}");
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private class FilesListView extends RefreshingView<SIAttachment> {
        private final WicketBuildContext ctx;
        private final ConfirmationModal  confirmationModal;

        public FilesListView(String id, IModel<SIList<SIAttachment>> listModel, WicketBuildContext ctx) {
            super(id, listModel);
            this.ctx = ctx;
            this.confirmationModal = ctx.getExternalContainer().newComponent(ConfirmationModal::new);
        }

        @SuppressWarnings("unchecked")
        public SIList<SIAttachment> getAttachmentList() {
            return (SIList<SIAttachment>) getDefaultModelObject();
        }

        @SuppressWarnings("unchecked")
        public IModel<SIList<SIAttachment>> getAttachmentListModel() {
            return (IModel<SIList<SIAttachment>>) getDefaultModel();
        }

        @Override
        protected Iterator<IModel<SIAttachment>> getItemModels() {
            final SIList<SIAttachment>       objList   = this.getAttachmentList();
            final List<IModel<SIAttachment>> modelList = new ArrayList<>();
            for (int i = 0; i < objList.size(); i++)
                modelList.add(new SInstanceListItemModel<>(this.getAttachmentListModel(), i));
            return modelList.iterator();
        }

        @Override
        protected void populateItem(Item<SIAttachment> item) {
            IModel<SIAttachment> itemModel = item.getModel();
            item.add(new DownloadLink("downloadLink", itemModel, downloader));
            item.add(new RemoveButton("remove_btn", itemModel));
        }

        private class RemoveButton extends AjaxButton {
            private final IModel<SIAttachment> itemModel;

            public RemoveButton(String id, IModel<SIAttachment> itemModel) {
                super(id);
                this.itemModel = itemModel;
            }

            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                IConsumer<AjaxRequestTarget> confirmationAction = (t) -> {
                    super.onSubmit(t, form);
                    SIAttachment file = itemModel.getObject();
                    removeFileFrom(FilesListView.this.getAttachmentList(), file.getFileId());
                    t.add(FileUploadListPanel.this);
                    t.add(fileList);
                };
                target.add(WicketFormUtils.findUpdatableComponentInHierarchy(confirmationModal));
                confirmationModal.show(target, confirmationAction);
            }

            @Override
            public boolean isVisible() {
                return ctx.getViewMode().isEdition();
            }
        }
    }

}
