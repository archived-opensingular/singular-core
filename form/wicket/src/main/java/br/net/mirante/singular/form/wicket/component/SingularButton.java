/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.component;

import org.opensingular.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import java.util.Optional;

public abstract class SingularButton extends AjaxButton {

    private final IModel<? extends SInstance> currentInstance;
    
    public SingularButton(String id, IModel<? extends SInstance> currentInstance) {
        super(id);
        this.currentInstance = currentInstance;
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        super.onSubmit(target, form);
        
        //HACK: a solução que eu preferia usar faria o onSubmit() final, mas daí quebraria a compatibilidade. Discutir.
        if (isShouldProcessFormSubmitWithoutValidation())
            WicketFormProcessing.onFormSubmit(form, Optional.of(target), getCurrentInstance(), false);
    }
    
    protected boolean isShouldProcessFormSubmitWithoutValidation() {
        return true;
    }

    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        super.onError(target, form);
        WicketFormProcessing.onFormError(form, Optional.of(target), getCurrentInstance());
    }

    protected IModel<? extends SInstance> getCurrentInstance() {
        return currentInstance;
    }
}
