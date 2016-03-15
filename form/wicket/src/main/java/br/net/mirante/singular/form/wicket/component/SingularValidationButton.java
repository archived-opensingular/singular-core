/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.component;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import java.util.Optional;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public abstract class SingularValidationButton extends SingularButton {

    public SingularValidationButton(String id) {
        super(id);
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

    public abstract IModel<? extends SInstance>  getCurrentInstance();
}
