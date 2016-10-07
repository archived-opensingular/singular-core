/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.validation;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.wicket.model.SInstanceRootModel;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;

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
