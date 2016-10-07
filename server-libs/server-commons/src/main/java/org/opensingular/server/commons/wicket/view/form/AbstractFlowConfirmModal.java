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

package org.opensingular.server.commons.wicket.view.form;


import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.lib.wicket.util.jquery.JQuery;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;

public abstract class AbstractFlowConfirmModal<T extends PetitionEntity> implements FlowConfirmModal<T> {

    protected final AbstractFormPage<T> formPage;

    public AbstractFlowConfirmModal(AbstractFormPage<T> formPage) {
        this.formPage = formPage;
    }

    /**
     * @param tn -> transition name
     * @param im -> instance model
     * @param vm -> view mode
     * @param m  -> modal
     * @return the new AjaxButton
     */
    protected FlowConfirmButton<T> newFlowConfirmButton(String tn, IModel<? extends SInstance> im, ViewMode vm, BSModalBorder m) {
        return new FlowConfirmButton<>(tn, "confirm-btn", im, ViewMode.EDIT.equals(vm), formPage, m);
    }

    protected void addDefaultConfirmButton(String tn, IModel<? extends SInstance> im, ViewMode vm, BSModalBorder modal) {
        final FlowConfirmButton<T> confirmButton = newFlowConfirmButton(tn, im, vm, modal);
        confirmButton.add($b.on("click", (c) -> JQuery.$(modal).append(".modal('hide');")));
        modal.addButton(BSModalBorder.ButtonStyle.CONFIRM, "label.button.confirm", confirmButton);
    }

    protected void addDefaultCancelButton(final BSModalBorder modal) {
        modal.addButton(
                BSModalBorder.ButtonStyle.CANCEl,
                "label.button.cancel",
                new AjaxButton("cancel-btn") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        modal.hide(target);
                    }
                }
        );
    }

}