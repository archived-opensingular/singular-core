/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

