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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import org.opensingular.form.SInstance;

public abstract class SingularSaveButton extends SingularValidationButton {

    private boolean validate;

    public SingularSaveButton(String id, IModel<? extends SInstance> currentInstance) {
        this(id, currentInstance, true);
    }

    public SingularSaveButton(String id, IModel<? extends SInstance> currentInstance, boolean validate) {
        super(id, currentInstance);
        this.validate = validate;
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        if (validate) {
            super.onSubmit(target, form);
        } else {
            onValidationSuccess(target, form, getCurrentInstance());
        }
    }

    protected boolean isValidate() {
        return validate;
    }
}

