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

import static org.apache.commons.lang3.ObjectUtils.*;
import static org.opensingular.form.wicket.mapper.attachment.FileUploadServlet.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.*;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.opensingular.form.SIList;
import org.opensingular.form.type.basic.AtrBasic;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.SingularEventsHandlers;
import org.opensingular.form.wicket.mapper.attachment.BaseJQueryFileUploadBehavior;
import org.opensingular.form.wicket.mapper.attachment.DownloadLink;
import org.opensingular.form.wicket.mapper.attachment.DownloadSupportedBehavior;
import org.opensingular.form.wicket.mapper.attachment.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.FileUploadServlet;
import org.opensingular.form.wicket.mapper.attachment.UploadResponseInfo;
import org.opensingular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.jquery.JQuery;
import org.opensingular.lib.wicket.util.resource.Icone;

/**
 * Lista  os uploads múltiplos.
 * <p>
 * O upload múltiplo executado via jquery para a servlet {@link FileUploadServlet} atualiza
 * o código no cliente via javascript por meio do código java script associado a esse painel FileListUploadPanel.js
 * Para manter os models atualizados o js cliente se comunica com esse panel através do {@link org.opensingular.form.wicket.mapper.attachment.list.FileListUploadPanel.AddFileBehavior}
 * para remover os arquivos e atualizar os models o js cliente se comunica com esse panel através do {@link org.opensingular.form.wicket.mapper.attachment.list.FileListUploadPanel.RemoveFileBehavior}
 *
 * @author fabricio, vinicius
 */
public class FileListUploadPanel extends Panel implements Loggable {

    private final Component                 fileField;
    private final WebMarkupContainer        fileList;
    private final AddFileBehavior           adder;
    private final RemoveFileBehavior        remover;
    private final DownloadSupportedBehavior downloader;
    private final WicketBuildContext        ctx;

    private UUID                            uploadId;

    public FileListUploadPanel(String id, IModel<SIList<SIAttachment>> model, WicketBuildContext ctx) {
        super(id, model);
        this.ctx = ctx;

        adder = new AddFileBehavior();
        remover = new RemoveFileBehavior(model);
        downloader = new DownloadSupportedBehavior(model);

        Label label = new Label("uploadLabel", $m.get(() -> ctx.getCurrentInstance().asAtr().getLabel()));
        label.add($b.visibleIfModelObject(StringUtils::isNotEmpty));
        label.add($b.onConfigure(c -> label.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (model.getObject().getAttributeValue(SPackageBasic.ATR_REQUIRED)) {
                    oldClasses.add("singular-form-required");
                } else {
                    oldClasses.remove("singular-form-required");
                }
                return oldClasses;
            }
        })));
        add(label);

        add((fileList = new WebMarkupContainer("fileList"))
            .add(new FilesListView("fileItem", model, ctx)));

        add(new WebMarkupContainer("button-container")
            .add((fileField = new WebMarkupContainer("fileUpload")))
            .add(new LabelWithIcon("fileUploadLabel", Model.of(""), Icone.PLUS, Model.of(fileField.getMarkupId())))
            .add($b.visibleIf(() -> ctx.getViewMode().isEdition())));

        add(ctx.createFeedbackCompactPanel("feedback"));
        add(new WebMarkupContainer("empty-box")
            .add(new WebMarkupContainer("select-file-link")
                .add(new Label("select-file-link-message", $m.ofValue("Selecione o(s) arquivo(s)")))
                .add($b.visibleIf(ctx.getViewMode()::isEdition))
                .add($b.onReadyScript(c -> JQuery.on(c, "click", JQuery.$(fileField).append(".click();")))))
            .add(new Label("empty-message", $m.ofValue("Nenhum arquivo adicionado"))
                .add($b.visibleIf(ctx.getViewMode()::isVisualization)))
            .add($b.visibleIf(() -> model.getObject().isEmpty())));

        add(adder, remover, downloader);
        add($b.classAppender("FileListUploadPanel"));
        add($b.classAppender("FileListUploadPanel_disabled", $m.get(() -> !this.isEnabledInHierarchy())));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        final FileUploadManager fileUploadManager = getFileUploadManager();

        if (uploadId == null || !fileUploadManager.findUploadInfo(uploadId).isPresent()) {
            final AtrBasic atrAttachment = getModelObject().getElementsType().asAtr();
            this.uploadId = fileUploadManager.createUpload(
                Optional.ofNullable(atrAttachment.getMaxFileSize()),
                Optional.empty(),
                Optional.ofNullable(atrAttachment.getAllowedFileTypes()));
        }
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
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(resourceRef("FileListUploadPanel.js")));
        response.render(OnDomReadyHeaderItem.forScript(generateInitJS()));
    }

    private String generateInitJS() {
        if (ctx.getViewMode().isEdition()) {
            return ""
            //@formatter:off
                + "\n $(function () { "
                + "\n   window.FileListUploadPanel.setup(" + new JSONObject()
                          .put("param_name"        , PARAM_NAME             )
                          .put("component_id"      , this.getMarkupId()     )
                          .put("file_field_id"     , fileField.getMarkupId())
                          .put("fileList_id"       , fileList.getMarkupId() )
                          .put("upload_url"        , uploadUrl()            )
                          .put("download_url"      , downloader.getUrl()    )
                          .put("add_url"           , adder.getUrl()         )
                          .put("remove_url"        , remover.getUrl()       )
                          .put("max_file_size"     , getMaxFileSize()       )
                          .put("allowed_file_types", getAllowedTypes()      )
                          .toString(2) + "); "
                + "\n });";
            //@formatter:on
        } else {
            return "";
        }
    }

    protected List<String> getAllowedTypes() {
        return defaultIfNull(
            getModelObject().getElementsType().asAtr().getAllowedFileTypes(),
            Collections.<String> emptyList());
    }

    private long getMaxFileSize() {
        return getModelObject().getElementsType().asAtr().getMaxFileSize();
    }

    @SuppressWarnings("unchecked")
    public IModel<SIList<SIAttachment>> getModel() {
        return (IModel<SIList<SIAttachment>>) getDefaultModel();
    }

    @SuppressWarnings("unchecked")
    public SIList<SIAttachment> getModelObject() {
        return (SIList<SIAttachment>) getDefaultModelObject();
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }

    private String uploadUrl() {
        return FileUploadServlet.getUploadUrl(getServletRequest(), uploadId);
    }

    private HttpServletRequest getServletRequest() {
        return (HttpServletRequest) getWebRequest().getContainerRequest();
    }

    private FileUploadManager getFileUploadManager() {
        return FileUploadManager.get(getServletRequest().getSession());
    }

    public static class LabelWithIcon extends Label {

        private final Icone          icon;
        private final IModel<String> forAttrValue;

        public LabelWithIcon(String id, IModel<?> model, Icone icon, IModel<String> forAttrValue) {
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
            super(FileListUploadPanel.this.getModel());
        }
        @Override
        public void onResourceRequested() {
            final HttpServletRequest httpReq = (HttpServletRequest) getWebRequest().getContainerRequest();
            final HttpServletResponse httpResp = (HttpServletResponse) getWebResponse().getContainerResponse();

            try {
                final String pFileId = getParamFileId("fileId").toString();
                final String pName = getParamFileId("name").toString();

                getLogger().debug("FileListUploadPanel.AddFileBehavior(fileId={},name={})", pFileId, pName);

                Optional<UploadResponseInfo> responseInfo = FileUploadServlet.consumeFile(httpReq, pFileId, file -> {
                    final SIAttachment siAttachment = currentInstance().addNew();
                    siAttachment.setContent(pName, file, file.length());
                    return new UploadResponseInfo(siAttachment);
                });

                responseInfo
                    .orElseThrow(() -> new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND))
                    .writeJsonObjectResponseTo(httpResp);

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
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private class FilesListView extends RefreshingView<SIAttachment> {
        private final WicketBuildContext ctx;
        public FilesListView(String id, IModel<SIList<SIAttachment>> listModel, WicketBuildContext ctx) {
            super(id, listModel);
            this.ctx = ctx;
        }
        @SuppressWarnings("unchecked")
        public SIList<SIAttachment> getAttackmentList() {
            return (SIList<SIAttachment>) getDefaultModelObject();
        }
        @SuppressWarnings("unchecked")
        public IModel<SIList<SIAttachment>> getAttackmentListModel() {
            return (IModel<SIList<SIAttachment>>) getDefaultModel();
        }

        @Override
        protected Iterator<IModel<SIAttachment>> getItemModels() {
            final SIList<SIAttachment> objList = this.getAttackmentList();
            final List<IModel<SIAttachment>> modelList = new ArrayList<>();
            for (int i = 0; i < objList.size(); i++)
                modelList.add(new SInstanceListItemModel<>(this.getAttackmentListModel(), i));
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
                super.onSubmit(target, form);
                SIAttachment file = itemModel.getObject();
                removeFileFrom(FilesListView.this.getAttackmentList(), file.getFileId());
                target.add(FileListUploadPanel.this);
                target.add(fileList);
            }
            @Override
            public boolean isVisible() {
                return ctx.getViewMode().isEdition();
            }
        }
    }

}
