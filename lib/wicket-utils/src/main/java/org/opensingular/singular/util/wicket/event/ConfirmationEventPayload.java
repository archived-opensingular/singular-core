/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.event;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import org.opensingular.singular.util.wicket.datatable.IBSAction;

public class ConfirmationEventPayload<T> implements Serializable {

    private final IModel<String> confirmationMessage;
    private final IBSAction<T> onConfirmed;
    private final IBSAction<T> onCanceled;
    private final IModel<T> actionModel;
    private final Component source;
    private final transient AjaxRequestTarget target;

    public ConfirmationEventPayload(
        Component source,
        AjaxRequestTarget target,
        IModel<String> confirmationMessage,
        IModel<T> actionModel,
        IBSAction<T> onConfirmed,
        IBSAction<T> onCanceled) {

        this.source = source;
        this.target = target;
        this.confirmationMessage = confirmationMessage;
        this.actionModel = actionModel;
        this.onConfirmed = onConfirmed;
        this.onCanceled = IBSAction.noopIfNull(onCanceled);
    }

    public ConfirmationEventPayload(
        Component source,
        AjaxRequestTarget target,
        IModel<String> confirmationMessage,
        IModel<T> actionModel,
        IBSAction<T> onConfirmed) {

        this(source, target, confirmationMessage, actionModel, onConfirmed, null);
    }

    public Component getSource() {
        return source;
    }
    public AjaxRequestTarget getTarget() {
        return target;
    }
    public IModel<String> getConfirmationMessage() {
        return confirmationMessage;
    }
    public void onConfirmed(AjaxRequestTarget target) {
        this.onConfirmed.execute(target, actionModel, source);
    }
    public void onCanceled(AjaxRequestTarget target) {
        this.onCanceled.execute(target, actionModel, source);
    }
}
