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

package org.opensingular.lib.wicket.util.ajax;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.wicket.util.util.MetronicUiBlockerAjaxCallListener;
import org.opensingular.lib.wicket.util.util.WicketEventUtils;

public abstract class ActionAjaxButton extends AjaxButton {

    private MetronicUiBlockerAjaxCallListener metronicUiBlocker;

    public ActionAjaxButton(String id, Form<?> form) {
        super(id, form);
    }

    public ActionAjaxButton(String id, IModel<String> model, Form<?> form) {
        super(id, model, form);
    }

    public ActionAjaxButton(String id, IModel<String> model) {
        super(id, model);
    }

    public ActionAjaxButton(String id) {
        super(id);
    }

    protected abstract void onAction(AjaxRequestTarget target, Form<?> form);

    public void setMetronicUiBlocker(MetronicUiBlockerAjaxCallListener metronicUiBlocker) {
        this.metronicUiBlocker = metronicUiBlocker;
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        super.onSubmit(target, form);
        onAction(target, form);
    }

    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        WicketEventUtils.sendAjaxErrorEvent(this, target);
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);
        if (metronicUiBlocker != null) {
            attributes.getAjaxCallListeners().add(metronicUiBlocker);
        }
    }
}
