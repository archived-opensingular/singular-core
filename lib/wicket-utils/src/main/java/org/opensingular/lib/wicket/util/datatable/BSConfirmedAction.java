/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.wicket.util.event.ConfirmationEventPayload;

public abstract class BSConfirmedAction<T> implements IBSAction<T> {
    private Component component;
    private IModel<String> confirmationMessage;

    public BSConfirmedAction(Component component, IModel<String> confirmationMessage) {
        this.component = component;
        this.confirmationMessage = confirmationMessage;
    }

    @Override
    public final void execute(AjaxRequestTarget target, IModel<T> model) {
        component.send(component, Broadcast.BUBBLE, new ConfirmationEventPayload<T>(
            component,
            target,
            confirmationMessage,
            model,
            this::onConfirmed,
            this::onCanceled));
    }

    protected abstract void onConfirmed(AjaxRequestTarget target, IModel<T> model);

    protected void onCanceled(AjaxRequestTarget target, IModel<T> model) {
        // do nothing
    }
}
