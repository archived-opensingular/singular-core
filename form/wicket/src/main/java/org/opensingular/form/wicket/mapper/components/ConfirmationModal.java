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
package org.opensingular.form.wicket.mapper.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.opensingular.lib.commons.lambda.IBiConsumer;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class ConfirmationModal extends Panel {
    protected final Form<?> confirmationForm = new Form<>("confirmationForm");
    protected final IModel<String> title = new Model<>();
    protected final IModel<String> bodyText = new Model<>();
    protected final BSModalBorder border = new BSModalBorder("confirmationModal", title);

    protected AjaxButton confirmButton;
    protected AjaxButton cancelButton;
    protected IBiConsumer<AjaxRequestTarget, Form<?>> confirmationAction;

    public ConfirmationModal(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        title.setObject(getTitleText());
        bodyText.setObject(getConfirmationMessage());
        add(confirmationForm);
        confirmationForm.add(border);
        border.add(new Label("message", bodyText));
        addCancelButton();
        addConfirmButton();
        setOutputMarkupId(true);
    }

    protected void addCancelButton() {
        border.addButton(BSModalBorder.ButtonStyle.CANCEL, $m.get(this::getCancelButtonLabel),
                cancelButton = (AjaxButton) new AjaxButton("cancel-btn", confirmationForm) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        border.hide(target);
                    }
                }.setDefaultFormProcessing(false));
    }

    protected void addConfirmButton() {
        border.addButton(BSModalBorder.ButtonStyle.CONFIRM, $m.get(this::getConfirmButtonLabel),
                confirmButton = new AjaxButton("confirm-btn", confirmationForm) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        confirmationAction.accept(target, form);
                        border.hide(target);
                    }
                });
    }

    protected String getCancelButtonLabel() {
        return new StringResourceModel("label.button.cancel").getString();
    }

    protected String getConfirmButtonLabel() {
        return new StringResourceModel("label.button.delete").getString();
    }

    protected String getConfirmationMessage() {
        return new StringResourceModel("label.delete.message").getString();
    }

    protected String getTitleText() {
        return new StringResourceModel("label.title.delete.item").getString();
    }

    public void show(AjaxRequestTarget target, IBiConsumer<AjaxRequestTarget, Form<?>> confirmationAction) {
        this.confirmationAction = confirmationAction;
        border.show(target);
    }
}