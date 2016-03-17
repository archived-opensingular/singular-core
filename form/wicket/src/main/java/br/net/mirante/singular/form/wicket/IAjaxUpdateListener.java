/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;

public interface IAjaxUpdateListener extends Serializable {

    void onUpdate(Component source, AjaxRequestTarget target, IModel<? extends SInstance> instanceModel);
    void onError(Component source, AjaxRequestTarget target, IModel<? extends SInstance> instanceModel);
}
