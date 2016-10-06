/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.behavior;

import org.opensingular.form.SInstance;
import org.opensingular.singular.form.wicket.IAjaxUpdateListener;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;

public final class AjaxUpdateChoiceBehavior extends AjaxFormChoiceComponentUpdatingBehavior {
    private final IAjaxUpdateListener listener;
    private final IModel<SInstance>  model;
    public AjaxUpdateChoiceBehavior(IModel<SInstance> model, IAjaxUpdateListener listener) {
        this.listener = listener;
        this.model = model;
    }
    @Override
    public void onUpdate(AjaxRequestTarget target) {
        listener.onProcess(this.getComponent(), target, model);
    }
}