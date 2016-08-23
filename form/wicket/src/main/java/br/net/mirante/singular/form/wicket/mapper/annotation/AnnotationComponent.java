/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.annotation;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;
import static org.apache.commons.lang3.StringUtils.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.net.mirante.singular.commons.util.FormatUtil;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.type.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.type.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.component.BFModalWindow;
import br.net.mirante.singular.form.wicket.model.ISInstanceAwareModel;
import br.net.mirante.singular.form.wicket.model.SInstanceFieldModel;
import br.net.mirante.singular.form.wicket.model.SInstanceRootModel;
import br.net.mirante.singular.form.wicket.model.SInstanceValueModel;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

/**
 * This is the visual component of an annotated field on screen.
 *
 * @author Fabricio Buzeto
 */
public class AnnotationComponent extends Panel {

    private final WicketBuildContext                context;
    private final ISInstanceAwareModel<SIComposite> referencedModel;
    private final SInstanceValueModel<String>       textModel;
    private final SInstanceValueModel<Boolean>      approvedModel;

    private boolean                                 keepOpened = false;

    private Component                               referencedComponent;
    private BSContainer<?>                          mainGrid;
    private ActionAjaxButton                        openModalButton;

    public AnnotationComponent(String id, ISInstanceAwareModel<SIComposite> referenced, WicketBuildContext context) {
        super(id);
        this.referencedModel = referenced;
        this.context = context;

        final SInstanceRootModel<SIAnnotation> annotationModel = new SInstanceRootModel<>(
            getAnnotation(referenced)
                .annotation()
                .setTargetId(referenced.getMInstancia().getId()));
        setModel(annotationModel);

        textModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(getModel(), "text"));
        approvedModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(getModel(), "isApproved"));
        
        add($b.classAppender("annotation-toggle-container btn-group open"));
        add($b.attr("style", "position:absolute; top:-10px; right:17px;"));
    }

    @Override
    public void detachModels() {
        super.detachModels();
        referencedModel.detach();
        textModel.detach();
        approvedModel.detach();
    }

    public AnnotationComponent setModel(SInstanceRootModel<SIAnnotation> model) {
        super.setDefaultModel(model);
        return this;
    }

    public ISInstanceAwareModel<SIComposite> getReferencedModel() {
        return referencedModel;
    }

    public void setReferencedComponent(Component referencedComponent) {
        this.referencedComponent = referencedComponent;
    }

    @SuppressWarnings("unchecked")
    public SInstanceRootModel<SIAnnotation> getModel() {
        return (SInstanceRootModel<SIAnnotation>) getDefaultModel();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(createToggleButton("toggleButton", getReferencedModel()));
        add(new Label("titulo", $m.ofValue(getTitle(getReferencedModel()))));
        add(new Label("comment_field", $m.get(() -> getTrimmedText())));
        add(createApprovalLabel("approvalLabel", approvedModel));
        add(openModalButton = createOpenModalButton("open_modal", createEditModal("annotationModal")));
        add(createDeleteModalButton("deleteAnnotationModal"));
    }

    private WebMarkupContainer createToggleButton(String id, IModel<SIComposite> model) {
        WebMarkupContainer toggleButton = new WebMarkupContainer(id);
        toggleButton.add($b.classAppender($m.get(() -> getToggleButtonCSS(model.getObject().asAtrAnnotation()))));
        return toggleButton;
    }

    private String getTrimmedText() {
        String text = defaultString(textModel.getObject());
        return (text.length() > 100) ? text.substring(0, 94) + " [...]" : text;
    }

    private static Label createApprovalLabel(String id, final SInstanceValueModel<Boolean> model) {
        IModel<String> labelTextModel = $m.get(() -> FormatUtil.booleanDescription(model.getObject(),
            "Aprovado",
            "Rejeitado"));
        IModel<String> labelStyleModel = $m.get(() -> FormatUtil.booleanDescription(model.getObject(),
            "annotation-status-approved",
            "annotation-status-rejected"));
        Label label = new Label(id, labelTextModel);
        label.add($b.classAppender(labelStyleModel));
        return label;
    }

    private BFModalWindow createEditModal(String id) {
        BFModalWindow annotationModal = new AnnotationModalWindow(id, getModel(), referencedModel, context, this);
        context.getExternalContainer().appendTag("div", true, null, annotationModal);
        return annotationModal;
    }

    private ActionAjaxButton createOpenModalButton(String id, final BFModalWindow annotationModal) {
        ActionAjaxButton button = new ActionAjaxButton(id) {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                keepOpened = true;
                annotationModal.show(target);
            }
        };
        button.add(new Label("open_icon")
            .add($b.classAppender($m.get(() -> {
                return (context.getRootContext().getAnnotationMode().editable())
                    ? "fa-pencil"
                    : "fa-expand";
            }))));
        return button;
    }

    private ActionAjaxButton createDeleteModalButton(String modalId) {
        final DeleteModal deleteModal = new DeleteModal(modalId);
        context.getExternalContainer().appendTag("div", true, null, deleteModal);
        return new ActionAjaxButton("trash_modal") {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                deleteModal.show(target);
            }
            @Override
            public boolean isVisible() {
                return context.getAnnotationMode().editable();
            }
        };
    }

    protected static String getTitle(ISInstanceAwareModel<?> referenced) {
        if (StringUtils.isNoneBlank(getAnnotation(referenced).label()))
            return getAnnotation(referenced).label();
        String label = labelOf(referenced);
        if (StringUtils.isNoneBlank(label))
            return String.format("Comentários sobre %s", label);
        return "Comentários";
    }

    private static AtrAnnotation getAnnotation(ISInstanceAwareModel<?> referenced) {
        return referenced.getMInstancia().asAtrAnnotation();
    }

    private static String labelOf(ISInstanceAwareModel<?> target) {
        return target.getMInstancia().asAtr().getLabel();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("annotation.js")));
        response.render(CssReferenceHeaderItem.forReference(resourceRef("annotation.css")));
        response.render(OnDomReadyHeaderItem.forScript(generateUpdateJS()));
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }

    protected String generateUpdateJS() {
        if (referencedComponent == null)
            return "";
        return "Annotation.create_or_update(" +
            "'#" + referencedComponent.getMarkupId() + "', " +
            "'#" + this.getMarkupId() + "'," +
            "'#" + openModalButton.getMarkupId() + "'," +
            "`" + textModel.getObject() + "`, " +
            " " + approvedModel.getObject() + ", " +
            " " + !context.getAnnotationMode().editable() + " " +
            "); \n";
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
//        if (keepOpened) {
//            this.add(WicketUtils.$b.attr("style", "float: left; display: block;"));
//        } else {
//            this.add(WicketUtils.$b.attr("style", "float: left; display: none;"));
//        }
//        keepOpened = false;
        //this.add(WicketUtils.$b.attrAppender("class", "portlet box sannotation-snipet-box", ""));
    }

    private static String getToggleButtonCSS(AtrAnnotation annotatedInstance) {
        if (annotatedInstance.hasAnnotation()) {
            if (annotatedInstance.annotation().getApproved() != null &&
                annotatedInstance.annotation().getApproved()) {
                return "btn-info";
            } else {
                return "btn-danger";
            }
        }
        return "btn-default";
    }

    public void setMainGrid(BSContainer<?> mainGrid) {
        this.mainGrid = mainGrid;
    }

    void setKeepOpened(boolean keepOpened) {
        this.keepOpened = keepOpened;
    }

    private final class DeleteModal extends BFModalWindow {
        private DeleteModal(String id) {
            super(id);
        }
        @Override
        protected void onInitialize() {
            super.onInitialize();
            final BFModalWindow thiz = this;
            thiz.setTitleText($m.ofValue("Você está prestes a remover este comentário."));
            thiz.setBody(new Label("alert", $m.ofValue("Deseja realmente prosseguir e apagá-lo?")));

            this.addButton(BSModalBorder.ButtonStyle.DANGER, $m.ofValue("Apagar"),
                new ActionAjaxButton("deleteBtn") {
                    @Override
                    protected void onAction(AjaxRequestTarget target, Form<?> form) {
                        ((SIAnnotation) AnnotationComponent.this.getModel().getObject()).clear();
                        target.add(AnnotationComponent.this.mainGrid);
                        target.appendJavaScript(AnnotationComponent.this.generateUpdateJS());
                        thiz.hide(target);
                    }
                });
            this.addLink(BSModalBorder.ButtonStyle.EMPTY, $m.ofValue("Cancelar"),
                new ActionAjaxLink<Void>("cancelDeleteBtn") {
                    @Override
                    protected void onAction(AjaxRequestTarget target) {
                        thiz.hide(target);
                    }
                });
        }
    }

    private static class AnnotationModalWindow extends BFModalWindow {

        private SInstanceValueModel<?>       textModel;
        private SInstanceValueModel<Boolean> approvedModel;
        private WicketBuildContext           context;
        private AnnotationComponent          parentComponent;
        private ISInstanceAwareModel<?>      referenced;

        public AnnotationModalWindow(String id,
            SInstanceRootModel<?> model,
            ISInstanceAwareModel<?> referenced,
            WicketBuildContext context,
            AnnotationComponent parentComponent) {
            super(id);
            setDefaultModel(model);
            this.referenced = referenced;
            this.context = context;
            this.parentComponent = parentComponent;
            setSize(BSModalBorder.Size.NORMAL);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();
            textModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(getDefaultModel(), "text"));
            approvedModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(getDefaultModel(), "isApproved"));

            setBody(createBody());

            if (context.getAnnotationMode().editable()) {
                this.addButton(BSModalBorder.ButtonStyle.BLUE, $m.ofValue("OK"),
                    createOkButton(parentComponent));
                this.addLink(BSModalBorder.ButtonStyle.EMPTY, $m.ofValue("Cancelar"),
                    createCancelButton());
            } else {
                this.addLink(BSModalBorder.ButtonStyle.EMPTY, $m.ofValue("Fechar"),
                    createCancelButton());
            }

            this.setCloseIconCallback(target -> parentComponent.setKeepOpened(false));
        }

        private BSContainer<?> createBody() {
            BSContainer<?> modalBody = new BSContainer<>("bogoMips");
            createFields(modalBody);
            return modalBody;
        }

        private void createFields(BSContainer<?> modalBody) {
            if (context.getAnnotationMode().editable()) {
                createCommentField(modalBody);
                createApprovedField(modalBody);
            } else {
                MultiLineLabel modalText = new MultiLineLabel("modalText", textModel);
                modalBody.appendTag("div", true, "class='sannotation-text-comment'", modalText);
                modalBody.appendTag("div", true, "", AnnotationComponent.createApprovalLabel("approval_field", approvedModel));
            }
        }

        private void createCommentField(BSContainer<?> modalBody) {
            TextArea<?> modalText = new TextArea<>("modalText", textModel);
            modalText.add(new Behavior() {
                @Override
                public void bind(Component component) {
                    super.bind(component);
                    component.add(
                        AttributeModifier.replace("onkeydown",
                            Model.of("window.Annotation.update_comment_box(event);")));
                }
            });
            modalBody.appendTag("textarea", true, "style='width: 100%;height: 60vh;' cols='15' ",
                modalText);
        }

        private void createApprovedField(BSContainer<?> modalBody) {

            String approved = getString("singular.annotation.approved");

            modalBody.appendTag("label", true, "class=\"control-label\"",
                new Label("approvalLabel", $m.ofValue(approved)));

            CheckBox modalApproval = new CheckBox("modalApproval", approvedModel);
            modalBody.appendTag("input", true, "type='checkbox' class='make-switch' data-on-color='info' data-off-color='danger'", modalApproval);

            modalApproval.add($b.attr("data-on-text", new ResourceModel("singular.annotation.yes")));
            modalApproval.add($b.attr("data-off-text", new ResourceModel("singular.annotation.no")));

        }

        @Override
        public void show(AjaxRequestTarget target) {
            setTitleText($m.ofValue(AnnotationComponent.getTitle(referenced)));

            super.show(target);
        }

        private ActionAjaxButton createOkButton(final AnnotationComponent parentComponent) {
            return new ActionAjaxButton("btn-ok") {
                @Override
                protected void onAction(AjaxRequestTarget target, Form<?> form) {
                    target.add(parentComponent.mainGrid);
                    AnnotationModalWindow.this.hide(target);
                    target.appendJavaScript(parentComponent.generateUpdateJS());
                }
            };
        }

        private ActionAjaxLink<Void> createCancelButton() {
            return new ActionAjaxLink<Void>("btn-cancelar") {
                @Override
                protected void onAction(AjaxRequestTarget target) {
                    parentComponent.setKeepOpened(false);
                    AnnotationModalWindow.this.hide(target);
                }
            };
        }
    }
}