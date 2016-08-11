/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.IAjaxUpdateListener;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;

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

    public static AjaxUpdateInputBehavior forValidate(IModel<SInstance> model, IAjaxUpdateListener listener) {
        return new AjaxUpdateInputBehavior(IWicketComponentMapper.SINGULAR_VALIDATE_EVENT, model, true, listener);
    }
    public static AjaxUpdateInputBehavior forProcess(IModel<SInstance> model, IAjaxUpdateListener listener) {
        return new AjaxUpdateInputBehavior(IWicketComponentMapper.SINGULAR_PROCESS_EVENT, model, false, listener);
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);
        //if (validateOnly)
        //    attributes.getExtraParameters().put("forceDisableAJAXPageBlock", true);
    }

    @Override
    public void onUpdate(AjaxRequestTarget target) {
        Component comp = this.getComponent();
        if (validateOnly) {
            listener.onValidate(comp, target, model);
        } else
            listener.onProcess(comp, target, model);
    }

}