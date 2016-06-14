/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.behavior;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.IAjaxUpdateListener;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;

public class AjaxUpdateInputBehavior extends AjaxFormComponentUpdatingBehavior {

    private final IAjaxUpdateListener listener;
    private final IModel<SInstance>   model;
    private final boolean             validateOnly;

    public AjaxUpdateInputBehavior(String event, IModel<SInstance> model, boolean validateOnly, IAjaxUpdateListener listener) {
        super(event);
        this.model = model;
        this.validateOnly = validateOnly;
        this.listener = listener;
    }

    @Override
    public void onUpdate(AjaxRequestTarget target) {
        if (validateOnly)
            listener.onValidate(this.getComponent(), target, model);
        else
            listener.onProcess(this.getComponent(), target, model);
    }

}