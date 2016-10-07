/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
