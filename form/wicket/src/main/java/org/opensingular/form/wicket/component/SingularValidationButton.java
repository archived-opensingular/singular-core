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

package org.opensingular.form.wicket.component;

import de.alpharogroup.wicket.js.addon.toastr.ToastrType;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisitor;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.wicket.util.toastr.ToastrHelper;

/**
 * This button should be used to validate the form and save.
 */
public abstract class SingularValidationButton extends SingularButton {

    public SingularValidationButton(String id, IModel<? extends SInstance> currentInstance) {
        super(id, currentInstance);
    }

    protected abstract void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel);

    protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
        new ToastrHelper(getPage()).
                addToastrMessage(ToastrType.ERROR, getString("message.save.error", null,
                        "O formulário não pode ser salvo enquanto houver correções a serem feitas."));
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        super.onSubmit(target, form);
        final MarkupContainer container = findSFormContainer();
        if (WicketFormProcessing.onFormSubmit(container, target, getCurrentInstance(), true)) {
            onValidationSuccess(target, form, getCurrentInstance());
        } else {
            onValidationError(target, form, getCurrentInstance());
        }
        target.add(container);
    }

    /**
     * Tries to find a SingularFormPanel nested inside the form, if no results is founded returns the form
     *
     * @return the container
     */
    private MarkupContainer findSFormContainer() {
        final MarkupContainer container = getForm()
                .visitChildren(SingularFormPanel.class, (IVisitor<SingularFormPanel, SingularFormPanel>) (a, v) -> v.stop(a));
        if (container != null) {
            return container;
        }
        return getForm();
    }

    @Override
    protected boolean isShouldProcessFormSubmitWithoutValidation() {
        return false;
    }
}