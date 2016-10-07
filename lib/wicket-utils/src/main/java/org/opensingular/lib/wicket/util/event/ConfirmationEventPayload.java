/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.wicket.util.event;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.wicket.util.datatable.IBSAction;

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
