/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket;

import org.opensingular.form.SInstance;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public interface IAjaxUpdateListener extends Serializable {

    //@formatter:off
    void onValidate(Component source, AjaxRequestTarget target, IModel<? extends SInstance> instanceModel);
    void onProcess (Component source, AjaxRequestTarget target, IModel<? extends SInstance> instanceModel);
    void onError   (Component source, AjaxRequestTarget target, IModel<? extends SInstance> instanceModel);
    //@formatter:on
}
