package br.net.mirante.singular.form.wicket.mapper.attachment.list;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.mapper.SingularEventsHandlers;
import br.net.mirante.singular.form.wicket.mapper.attachment.BaseJQueryFileUploadBehavior;
import br.net.mirante.singular.form.wicket.mapper.attachment.DownloadLink;
import br.net.mirante.singular.form.wicket.mapper.attachment.DownloadSupportedBehavior;
import br.net.mirante.singular.form.wicket.mapper.attachment.DownloadUtil;
import br.net.mirante.singular.form.wicket.mapper.attachment.FileUploadServlet;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.model.SInstanceItemListaModel;
import br.net.mirante.singular.util.wicket.jquery.JQuery;
import br.net.mirante.singular.util.wicket.resource.Icone;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.StyleAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static br.net.mirante.singular.form.wicket.mapper.attachment.FileUploadServlet.PARAM_NAME;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem.forReference;

/**
 * Lista  os uploads múltiplos.
 * <p>
 * O upload múltiplo executado via jquery para a servlet {@link FileUploadServlet} atualiza
 * o código no cliente via javascript por meio do código java script associado a esse painel FileListUploadPanel.js
 * Para manter os models atualizados o js cliente se comunica com esse panel através do {@link br.net.mirante.singular.form.wicket.mapper.attachment.list.FileListUploadPanel.AddFileBehavior}
 * para remover os arquivos e atualizar os models o js cliente se comunica com esse panel através do {@link br.net.mirante.singular.form.wicket.mapper.attachment.list.FileListUploadPanel.RemoveFileBehavior}
 *
 * @author fabricio, vinicius
 */
public class FileListUploadPanel extends Panel implements Loggable {

    private FileUploadField           fileField;
    private WebMarkupContainer        fileList;
    private AddFileBehavior           adder;
    private RemoveFileBehavior        remover;
    private WicketBuildContext        ctx;
    private DownloadSupportedBehavior downloader;


    private Label buildUploadLabel() {
        return new Label("uploadLabel", Model.of(ObjectUtils.defaultIfNull(ctx.getCurrentInstance().asAtr().getLabel(), StringUtils.EMPTY))) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisible(StringUtils.isNotEmpty(getDefaultModelObjectAsString()));
            }
        };
    }

    private WebMarkupContainer buildEmptyBox(IModel<SIList<SIAttachment>> model, FileUploadField fileField) {
        return (WebMarkupContainer) new WebMarkupContainer("empty-box") {
            @Override
            public boolean isVisible() {
                return model.getObject().isEmpty();
            }
        }.add(new WebMarkupContainer("select-file-link") {{
            add($b.onReadyScript(() -> JQuery.on(this, "click", JQuery.$(fileField).append(".click();"))));
        }}.add(new Label("empty-message", "Selecione o arquivo.")));
    }


    private WebMarkupContainer buildFileList(IModel<SIList<SIAttachment>> model) {
        final WebMarkupContainer fileList = new WebMarkupContainer("fileList");
        fileList.add(new FilesListView(model, ctx));
        return fileList;
    }

    private WebMarkupContainer buildButtonContainer(IModel<SIList<SIAttachment>> model) {
        final WebMarkupContainer buttonContainer = new WebMarkupContainer("button-container");
        buttonContainer.add(fileField = new FileUploadField("fileUpload", dummyModel()));
        buttonContainer.add(new LabelWithIcon("fileUploadLabel", Model.of(""), Icone.PLUS, Model.of(fileField.getMarkupId())) {
            @Override
            public boolean isVisible() {
                return ctx.getViewMode().isEdition();
            }
        });
        buttonContainer.add(new StyleAttributeModifier() {
            @Override
            protected Map<String, String> update(Map<String, String> oldStyles) {
                final Map<String, String> newStyles = new HashMap<>(oldStyles);
                if (model.getObject().isEmpty()) {
                    newStyles.put("display", "none");
                } else {
                    newStyles.remove("display");
                }
                return newStyles;
            }
        });
        fileField.add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_MOUSEDOWN_HANDLERS));
        return buttonContainer;
    }


    public FileListUploadPanel(String id, IModel<SIList<SIAttachment>> model,
                               WicketBuildContext ctx) {
        super(id, model);
        this.ctx = ctx;
        add(buildUploadLabel());
        add(fileList = buildFileList(model));
        add(buildButtonContainer(model));
        add(buildEmptyBox(model, fileField));
        add(adder = new AddFileBehavior());
        add(remover = new RemoveFileBehavior());
        add(downloader = new DownloadSupportedBehavior(model));
    }

    private static void removeFileFrom(SIList<SIAttachment> list, String fileId) {
        SIAttachment file = findFileByID(list, fileId);
        if (file != null) {
            list.remove(file);
        }
    }

    private static SIAttachment findFileByID(SIList<SIAttachment> list, String fileId) {
        SIAttachment file = null;
        for (SIAttachment a : list) {
            if (fileId.equals(a.getFileId())) {
                file = a;
                break;
            }
        }
        return file;
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
        if (ctx.getViewMode().isEdition()) {
            return ""
                    //@formatter:off
                    + "\n $(function () { "
                    + "\n   var params = { "
                    + "\n     param_name : '" + PARAM_NAME + "', "
                    + "\n     component_id: '" + this.getMarkupId() + "', "
                    + "\n     file_field_id: '" + fileField.getMarkupId() + "', "
                    + "\n     fileList_id: '" + fileList.getMarkupId() + "', "
                    + "\n     upload_url : '" + uploadUrl() + "', "
                    + "\n     download_url : '" + downloader.getUrl() + "', "
                    + "\n     add_url : '" + adder.getUrl() + "', "
                    + "\n     remove_url : '" + remover.getUrl() + "', "
                    + "\n     max_file_size: " + getMaxFileSize() + "  "
                    + "\n   }; "
                    + "\n   window.FileListUploadPanel.setup(params); "
                    + "\n });";
            //@formatter:on
        } else {
            return "";
        }
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
        String contextPath = getWebApplication().getServletContext().getContextPath();
        return contextPath + FileUploadServlet.UPLOAD_URL;
    }

    private IMInstanciaAwareModel dummyModel() {
        return new IMInstanciaAwareModel() {
            @Override
            public Object getObject() {
                return null;
            }

            @Override
            public void setObject(Object object) {
            }

            @Override
            public void detach() {
            }

            @Override
            public SInstance getMInstancia() {
                return (SInstance) getDefaultModel().getObject();
            }
        };
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
            super((IModel<SIList<SIAttachment>>) FileListUploadPanel.this.getDefaultModel());
        }

        @Override
        public void onResourceRequested() {
            try {
                SIAttachment siAttachment = currentInstance().addNew();
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

    private class RemoveFileBehavior extends BaseJQueryFileUploadBehavior<SIList<SIAttachment>> {

        public RemoveFileBehavior() {
            super((IModel<SIList<SIAttachment>>) FileListUploadPanel.this.getDefaultModel());
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
        private final IModel<SIList<SIAttachment>> model;
        private final WicketBuildContext           ctx;

        public FilesListView(IModel<SIList<SIAttachment>> model, WicketBuildContext ctx) {
            super("fileItem", model);
            this.model = model;
            this.ctx = ctx;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Iterator<IModel<SIAttachment>> getItemModels() {
            return new TransformIterator(model.getObject().iterator(), input -> new SInstanceItemListaModel<>(model, model.getObject().indexOf((SInstance) input)));
        }

        @Override
        protected void populateItem(Item<SIAttachment> item) {
            IMInstanciaAwareModel itemModel = (SInstanceItemListaModel) item.getModel();
            item.add(new DownloadLink("downloadLink", itemModel, downloader));
            item.add(new RemoveButton(itemModel));
        }

        private class RemoveButton extends AjaxButton {
            private final IMInstanciaAwareModel itemModel;

            public RemoveButton(IMInstanciaAwareModel itemModel) {
                super("remove_btn");
                this.itemModel = itemModel;
            }

            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                SIAttachment file = (SIAttachment) itemModel.getObject();

                removeFileFrom(model.getObject(), file.getFileId());

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
