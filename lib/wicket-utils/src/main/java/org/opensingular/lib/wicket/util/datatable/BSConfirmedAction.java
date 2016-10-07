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
