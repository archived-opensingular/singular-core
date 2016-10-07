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

package org.opensingular.form.wicket.mapper.annotation;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.ResourceModel;

import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxLink;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.jquery.JQuery;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

class AnnotationModalWindow extends BFModalWindow {

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

    class CancelOrCloseButton extends ActionAjaxLink<Void> {
        private CancelOrCloseButton(String id) {
            super(id);
        }
        @Override
        protected void onAction(AjaxRequestTarget target) {
            AnnotationModalWindow.this.hide(target);
            target.appendJavaScript(JQuery.$(annotationComponent)
                + ".find('a:visible:first').each(function(){this.focus();});");
        }
    }
    class OkButton extends ActionAjaxButton {
        private final AnnotationComponent parentComponent;
        private OkButton(String id, AnnotationComponent parentComponent) {
            super(id);
            this.parentComponent = parentComponent;
        }
        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            target.add(parentComponent);
            AnnotationModalWindow.this.hide(target);
            target.appendJavaScript(JQuery.$(annotationComponent)
                + ".find('a:visible:first').each(function(){this.focus();});");
        }
    }

}