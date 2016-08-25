/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.annotation;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;
import static org.apache.commons.lang3.BooleanUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

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

    public AnnotationComponent(String id, WicketBuildContext context, ISInstanceAwareModel<SIComposite> referenced) {
        super(id);
        this.context = context;
        this.referencedModel = referenced;

        setAnnotationModel(new SInstanceRootModel<>(
            referenced.getMInstancia().asAtrAnnotation()
                .annotation()
                .setTargetId(referenced.getMInstancia().getId())));

        textModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(getAnnotationModel(), "text"));
        approvedModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(getAnnotationModel(), "isApproved"));

        add($b.classAppender("annotation-toggle-container btn-group open"));
        add($b.attr("style", "position:absolute; top:-10px; right:17px;"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final boolean editable = context.getAnnotationMode().editable();

        final AnnotationModalWindow editAnnotationModal = new AnnotationModalWindow("editAnnotationModal", AnnotationComponent.this, editable);
        final RemoveAnnotationModal removeAnnotationModal = new RemoveAnnotationModal("removeAnnotationModal");
        context.getExternalContainer()
            .appendTag("div", true, null, editAnnotationModal)
            .appendTag("div", true, null, removeAnnotationModal);

        add(new NewAnnotationButton("emptyButton", editAnnotationModal, editable)
            .add($b.visibleIf(() -> !hasAnnotationText())));

        add(new WebMarkupContainer("dropDownContainer")
            .add(new WebMarkupContainer("toggleButton")
                .add($b.classAppender($m.get(() -> getToggleButtonCSS(getReferencedModel())))))
            .add(new Label("title", $m.ofValue(getTitle(getReferencedModel()))))
            .add(new Label("annotationText", $m.get(() -> getTrimmedText())))
            .add(new ApprovalStatusLabel("approvalLabel", approvedModel))
            .add(new EditAnnotationButton("editButton", editAnnotationModal, editable))
            .add(new RemoveAnnotationButton("removeButton", removeAnnotationModal).setVisible(editable))
            .add($b.visibleIf(() -> hasAnnotationText())));
    }

    @Override
    public void detachModels() {
        super.detachModels();
        referencedModel.detach();
        textModel.detach();
        approvedModel.detach();
    }

    private boolean hasAnnotationText() {
        final SIAnnotation annotation = getAnnotationModel().getObject();
        return (annotation != null) && (!annotation.isEmptyOfData());
    }

    public AnnotationComponent setAnnotationModel(SInstanceRootModel<SIAnnotation> model) {
        super.setDefaultModel(model);
        return this;
    }

    public ISInstanceAwareModel<SIComposite> getReferencedModel() {
        return referencedModel;
    }

    @SuppressWarnings("unchecked")
    public SInstanceRootModel<SIAnnotation> getAnnotationModel() {
        return (SInstanceRootModel<SIAnnotation>) getDefaultModel();
    }

    private String getTrimmedText() {
        String text = defaultString(textModel.getObject());
        return (text.length() > 100) ? text.substring(0, 94) + " [...]" : text;
    }

    protected static String getTitle(ISInstanceAwareModel<SIComposite> referenced) {
        final String annotationLabel = referenced.getMInstancia().asAtrAnnotation().label();
        if (isNotBlank(annotationLabel))
            return annotationLabel;

        final String referencedLabel = referenced.getMInstancia().asAtr().getLabel();
        if (isNotBlank(referencedLabel))
            return String.format("Comentários sobre %s", referencedLabel);

        return "Comentários";
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

    private static String getToggleButtonCSS(ISInstanceAwareModel<SIComposite> referencedModel) {
        AtrAnnotation isInstanceAwareModel = referencedModel.getObject().asAtrAnnotation();
        if (!isInstanceAwareModel.hasAnnotation())
            return "btn-default";
        else if (isTrue(isInstanceAwareModel.annotation().getApproved()))
            return "btn-info";
        else
            return "btn-danger";
    }

    private static final class ApprovalStatusLabel extends Label {
        private ApprovalStatusLabel(String id, SInstanceValueModel<Boolean> approvedModel) {
            super(id, $m.get(
                () -> FormatUtil.booleanDescription(approvedModel.getObject(),
                    "Aprovado",
                    "Rejeitado")));
            this.add($b.classAppender($m.get(
                () -> FormatUtil.booleanDescription(approvedModel.getObject(),
                    "annotation-status-approved",
                    "annotation-status-rejected"))));
        }
    }

    private final class NewAnnotationButton extends ActionAjaxButton {
        private final BFModalWindow annotationModal;
        private NewAnnotationButton(String id, BFModalWindow annotationModal, boolean editable) {
            super(id);
            this.annotationModal = annotationModal;
            this.add($b.classAppender($m.get(() -> (editable) ? "fa-pencil" : "fa-expand")));
        }
        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            annotationModal.show(target);
        }
    }

    private final class EditAnnotationButton extends ActionAjaxButton {
        private final BFModalWindow annotationModal;
        private EditAnnotationButton(String id, BFModalWindow annotationModal, boolean editable) {
            super(id);
            this.annotationModal = annotationModal;
            this.add(new Label("editIcon")
                .add($b.classAppender($m.get(() -> (editable) ? "fa-pencil" : "fa-expand"))));
        }
        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            annotationModal.show(target);
        }
    }

    private final class RemoveAnnotationButton extends ActionAjaxButton {
        private final BFModalWindow deleteModal;
        private RemoveAnnotationButton(String id, BFModalWindow deleteModal) {
            super(id);
            this.deleteModal = deleteModal;
        }
        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            deleteModal.show(target);
        }
    }

    private final class RemoveAnnotationModal extends BFModalWindow {
        private RemoveAnnotationModal(String id) {
            super(id);
        }
        @Override
        protected void onInitialize() {
            super.onInitialize();
            this.setTitleText($m.ofValue("Você está prestes a remover este comentário."))
                .setBody(new Label("alert", $m.ofValue("Deseja realmente prosseguir e apagá-lo?")))
                .addButton(BSModalBorder.ButtonStyle.DANGER, $m.ofValue("Apagar"),
                    new ActionAjaxButton("deleteBtn") {
                        @Override
                        protected void onAction(AjaxRequestTarget target, Form<?> form) {
                            ((SIAnnotation) AnnotationComponent.this.getAnnotationModel().getObject()).clear();
                            target.add(AnnotationComponent.this);
                            RemoveAnnotationModal.this.hide(target);
                        }
                    })
                .addLink(BSModalBorder.ButtonStyle.EMPTY, $m.ofValue("Cancelar"),
                    new ActionAjaxLink<Void>("cancelDeleteBtn") {
                        @Override
                        protected void onAction(AjaxRequestTarget target) {
                            RemoveAnnotationModal.this.hide(target);
                        }
                    });
        }
    }

    private static class AnnotationModalWindow extends BFModalWindow {

        private final boolean             editable;
        private final AnnotationComponent annotationComponent;

        public AnnotationModalWindow(String id, AnnotationComponent annotationComponent, boolean editable) {
            super(id);
            this.editable = editable;
            this.annotationComponent = annotationComponent;
            setSize(BSModalBorder.Size.NORMAL);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();
            final SInstanceValueModel<String> textModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(annotationComponent.getAnnotationModel(), "text"));
            final SInstanceValueModel<Boolean> approvedModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(annotationComponent.getAnnotationModel(), "isApproved"));

            if (editable) {

                final TextArea<?> comment = new TextArea<>("comment", textModel);
                final Label label = new Label("label", $m.ofValue(getString("singular.annotation.approved")));
                final Component check = new CheckBox("approvalCheck", approvedModel)
                    .add($b.attr("data-on-text", new ResourceModel("singular.annotation.yes")))
                    .add($b.attr("data-off-text", new ResourceModel("singular.annotation.no")));

                this
                    .setBody(new BSContainer<>("body")
                        .appendTag("textarea", true, "style='width: 100%;height: 60vh;' cols='15' ", comment)
                        .appendTag("label", true, "class=\"control-label\"", label)
                        .appendTag("input", true, "type='checkbox' class='make-switch' data-on-color='info' data-off-color='danger'", check))
                    .addButton(BSModalBorder.ButtonStyle.BLUE, $m.ofValue("OK"), new OkButton("btn-ok", annotationComponent))
                    .addLink(BSModalBorder.ButtonStyle.EMPTY, $m.ofValue("Cancelar"), new CancelOrCloseButton("btn-cancelar"));

            } else {

                this
                    .setBody(new BSContainer<>("body")
                        .appendTag("div", true, "class='sannotation-text-comment'", new MultiLineLabel("text", textModel))
                        .appendTag("div", true, "", new ApprovalStatusLabel("approvalLabel", approvedModel)))
                    .addLink(BSModalBorder.ButtonStyle.EMPTY, $m.ofValue("Fechar"), new CancelOrCloseButton("cancel"));
            }
        }

        @Override
        public void show(AjaxRequestTarget target) {
            setTitleText($m.ofValue(AnnotationComponent.getTitle(annotationComponent.getReferencedModel())));
            super.show(target);
        }

        private final class CancelOrCloseButton extends ActionAjaxLink<Void> {
            private CancelOrCloseButton(String id) {
                super(id);
            }
            @Override
            protected void onAction(AjaxRequestTarget target) {
                AnnotationModalWindow.this.hide(target);
            }
        }
        private final class OkButton extends ActionAjaxButton {
            private final AnnotationComponent parentComponent;
            private OkButton(String id, AnnotationComponent parentComponent) {
                super(id);
                this.parentComponent = parentComponent;
            }
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                target.add(parentComponent);
                AnnotationModalWindow.this.hide(target);
            }
        }

    }
}