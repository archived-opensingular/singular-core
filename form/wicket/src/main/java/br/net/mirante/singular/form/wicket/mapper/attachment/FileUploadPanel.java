/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSWellBorder;
import br.net.mirante.singular.util.wicket.upload.SFileUploadField;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.time.Duration;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

/**
 * FileUploadPanel
 * Classe responsavel por renderizar o conteudo do componente de anexo do singular
 */
public class FileUploadPanel extends Panel {

    private final static String CLICK_DELEGATE_SCRIPT_TEMPLATE = "$('#%s').on('click', function(){$('#%s').click();});";

    /**
     * Model principal, deve ser provido na construção da instancia
     */
    private final IModel<SIAttachment> model;

    /**
     * Componente de upload do wicket
     */
    private final SFileUploadField uploadField = new SFileUploadField("fileUpload") {
        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(viewMode.isEdition());
        }
    };

    /**
     * Markup do botão de escolha
     */
    private final WebMarkupContainer chooseFieldButton = new WebMarkupContainer("choose");

    /**
     * Agrupa os componentes, dinamicamente pode ser um inputgroup
     */
    private final WebMarkupContainer panelWrapper = new WebMarkupContainer("panelWrapper") {
        @Override
        protected void onInitialize() {
            super.onInitialize();
            if (viewMode.isEdition()) {
                add($b.classAppender("input-group"));
            }
        }
    };

    /**
     * Markup do field que mostra o link para download
     */
    private final WebMarkupContainer fileDummyField;

    /**
     * DownloadLink, escreve o arquivo do SIAttachment.
     */
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

    /**
     * Label do nome do arquivo
     */
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

    /**
     * Markup do botão de remover arquivos,
     * utilizia  methodo cleaninstance da instancia
     */
    private final AjaxButton removeFileButton = new AjaxButton("removeFileButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onSubmit(target, form);
            model.getObject().clearInstance();
            if (model.getObject().getParent() instanceof SIList) {
                final SIList parent = (SIList) model.getObject().getParent();
                parent.remove(parent.indexOf(model.getObject()));
                target.add(form);
            } else {
                target.add(fileDummyField, fileName, removeFileButton, chooseFieldButton);
            }
        }
    };

    private final ViewMode viewMode;

    /**
     * Construdor do Panel, todos os parametros são obrigatorios
     *
     * @param id    do compoente
     * @param model que contem o SIAttachment
     */
    public FileUploadPanel(String id, IModel<SIAttachment> model, ViewMode viewMode) {
        super(id);
        this.model = model;
        this.viewMode = viewMode;
        uploadField.setModel(new WrapperAwareModel(model));
        fileDummyField = buildFileDummyField("fileDummyField");
        add(uploadField, panelWrapper.add(chooseFieldButton, removeFileButton, fileDummyField.add(buildAttachmentShadow(), downloadLink.add(fileName))));
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

    /**
     * Inicialização do panel, somente configura o outputmarkid
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        fileDummyField.setOutputMarkupId(true);
        fileName.setOutputMarkupId(true);
        chooseFieldButton.setOutputMarkupId(true);
        removeFileButton.setOutputMarkupId(true);
        fileName.add($b.onConfigure(c -> c.add($b.attr("title", c.getDefaultModel()))));
        configureBehaviours();
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

    /**
     * Adiciona os behaviours aos componentes
     */
    protected void configureBehaviours() {
        super.onConfigure();
        uploadField.add(new AjaxFormSubmitBehavior("change") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                final FileUpload upload = uploadField.getFileUpload();
                if (upload != null) {
                    model.getObject().setContent(upload.getBytes());
                    model.getObject().setFileName(upload.getClientFileName());
                    target.add(fileDummyField, fileName, chooseFieldButton, removeFileButton);
                }
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.getAjaxCallListeners().add(new AjaxCallListener() {
                    @Override
                    public CharSequence getPrecondition(Component component) {
                        return generateOnchangeValidationJS(
                                "$('#" + uploadField.getMarkupId() + "')[0]");
                    }
                });
            }
        });
        chooseFieldButton.add(new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                response.render(OnDomReadyHeaderItem.forScript(String.format(CLICK_DELEGATE_SCRIPT_TEMPLATE,
                        chooseFieldButton.getMarkupId(true), uploadField.getMarkupId(true))));
            }
        });
        chooseFieldButton.add($b.onConfigure(c -> c.setVisible(model.getObject().isEmptyOfData() && viewMode.isEdition())));
        removeFileButton.add($b.onConfigure(c -> c.setVisible(!model.getObject().isEmptyOfData() && viewMode.isEdition())));
    }

    private String generateOnchangeValidationJS(String element) {
        Bytes max = getApplication().getApplicationSettings().getDefaultMaximumUploadSize();
        return "FileUploadPanel.validateInputFile( " + element + " ," + max.bytes() + ")";
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("FileUploadPanel.js")));
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }

    /**
     * @return o field principal do painel
     */
    public SFileUploadField getUploadField() {
        return uploadField;
    }

    /**
     * Wrapper model para utilização em um fileuploadField, adiciona o comportamento de
     * IMInstanciaAwareModel, tornando capaz de recuperar o SIAttachment
     */
    class WrapperAwareModel implements IMInstanciaAwareModel<List<FileUpload>> {

        /**
         * Model que armazena o  SIAttachment
         */
        private final IModel<SIAttachment> realModeal;

        /**
         * Arquivos
         */
        private List<FileUpload> files;

        WrapperAwareModel(IModel<SIAttachment> realModeal) {
            this.realModeal = realModeal;
        }

        @Override
        public List<FileUpload> getObject() {
            return files;
        }

        @Override
        public void setObject(List<FileUpload> files) {
            this.files = files;
        }

        @Override
        public void detach() {

        }

        @Override
        public SInstance getMInstancia() {
            return realModeal.getObject();
        }
    }
}
