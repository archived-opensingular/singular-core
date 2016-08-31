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
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
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

        SInstance referencedInstance = referenced.getMInstancia();
        SIAnnotation annotationInstance = referencedInstance.asAtrAnnotation().annotation()
            .setTargetId(referencedInstance.getId());
        setAnnotationModel(new SInstanceRootModel<>(annotationInstance));

        textModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(getAnnotationModel(), "text"));
        approvedModel = new SInstanceValueModel<>(new SInstanceFieldModel<>(getAnnotationModel(), "isApproved"));

        add($b.classAppender("annotation-toggle-container btn-group"));
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
    protected void onConfigure() {
        super.onConfigure();
        add(new AttributeAppender("class", "open") {
            @Override
                public boolean isEnabled(Component component) {
                    return super.isEnabled(component) && hasAnnotationText();
                }
            @Override
            public boolean isTemporary(Component component) {
                return true;
            }
        });
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
        return (annotation != null) && (isNotBlank(annotation.getText()));
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

    private static String getToggleButtonCSS(ISInstanceAwareModel<SIComposite> referencedModel) {
        AtrAnnotation isInstanceAwareModel = referencedModel.getObject().asAtrAnnotation();
        if (!isInstanceAwareModel.hasAnnotation())
            return "btn-default";
        else if (isTrue(isInstanceAwareModel.annotation().getApproved()))
            return "btn-info";
        else
            return "btn-danger";
    }

    private final class NewAnnotationButton extends ActionAjaxButton {
        private final BFModalWindow annotationModal;
        private NewAnnotationButton(String id, BFModalWindow annotationModal, boolean editable) {
            super(id);
            this.annotationModal = annotationModal;
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
            this.add(new Label("editIcon"));
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
}