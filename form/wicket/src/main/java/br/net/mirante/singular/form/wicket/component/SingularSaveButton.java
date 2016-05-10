/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.component;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.internal.xml.MElement;
import br.net.mirante.singular.form.io.MformPersistenciaXML;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public abstract class SingularSaveButton extends SingularValidationButton {

    private boolean validate;

    public SingularSaveButton(String id) {
        this(id, true);
    }

    public SingularSaveButton(String id, boolean validate) {
        super(id);
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

    @Override
    protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
        MElement rootXml = MformPersistenciaXML.toXML(getCurrentInstance().getObject());
        handleSaveXML(target, rootXml);
    }
    
    protected abstract void handleSaveXML(AjaxRequestTarget target, MElement xml);

    protected boolean isValidate() {
        return validate;
    }
}

