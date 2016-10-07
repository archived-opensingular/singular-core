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

import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import java.util.Optional;

public abstract class SingularValidationButton extends SingularButton {

    public SingularValidationButton(String id, IModel<? extends SInstance> currentInstance) {
        super(id, currentInstance);
    }

    protected abstract void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance>  instanceModel);
    protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {}

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        super.onSubmit(target, form);
        if (WicketFormProcessing.onFormSubmit(form, Optional.of(target), getCurrentInstance(), true)) {
            onValidationSuccess(target, form, getCurrentInstance());
        } else {
            onValidationError(target, form, getCurrentInstance());
        }
        target.add(form);
    }
    
    @Override
    protected boolean isShouldProcessFormSubmitWithoutValidation() {
        return false;
    }

}
