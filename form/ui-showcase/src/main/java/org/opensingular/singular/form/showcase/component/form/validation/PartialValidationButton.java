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

package org.opensingular.singular.form.showcase.component.form.validation;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.validation.InstanceValidationContext;
import org.opensingular.form.wicket.model.SInstanceRootModel;
import org.opensingular.form.wicket.util.WicketFormProcessing;

public class PartialValidationButton extends AjaxButton {

    private final IModel<? extends SInstance> currentInstance;

    public PartialValidationButton(String id, IModel<? extends SInstance> currentInstance) {
        super(id);
        this.currentInstance = currentInstance;
    }

    //@destacar:bloco
    protected void addValidationErrors(AjaxRequestTarget target, Form<?> form, SInstance instance) {
        final SInstance obrigatorio1 = ((SIComposite) instance).getField("obrigatorio_1");
        InstanceValidationContext validationContext = new InstanceValidationContext();
        validationContext.validateSingle(obrigatorio1);
        WicketFormProcessing.updateValidationFeedbackOnDescendants(
            Optional.ofNullable(target),
            form,
            new SInstanceRootModel<>(obrigatorio1),
            validationContext.getErrorsByInstanceId());
    }
    //@destacar:fim

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        super.onSubmit(target, form);
        addValidationErrors(target, form, currentInstance.getObject());
        target.add(form);
    }

    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        super.onError(target, form);
        WicketFormProcessing.onFormError(form, Optional.of(target), currentInstance);
    }
}
