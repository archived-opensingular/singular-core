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
package org.opensingular.form.wicket.mapper.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.scripts.Scripts;

public class ConfirmationModal extends AbstractConfirmationModal {

    protected IConsumer<AjaxRequestTarget> confirmationAction;

    public ConfirmationModal(String id) {
        super(id);
    }

    protected void onConfirm(AjaxRequestTarget target) {
        confirmationAction.accept(target);
    }

    public void show(AjaxRequestTarget target, IConsumer<AjaxRequestTarget> confirmationAction) {
        this.confirmationAction = confirmationAction;
        border.show(target);
        target.appendJavaScript(Scripts.multipleModalBackDrop());
    }
}