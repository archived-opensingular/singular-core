/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.wicket.util.modal;

import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.ButtonStyle;

public interface IOpenModalEvent {

    String getModalTitle();

    Component getBodyContent(String id);

    AjaxRequestTarget getTarget();

    Iterator<ButtonDef> getFooterButtons(IConsumer<AjaxRequestTarget> closeCallback);

    public static class ButtonDef {
        public final ButtonStyle      style;
        public final IModel<String>   label;
        public final ActionAjaxButton button;
        public ButtonDef(ButtonStyle style, IModel<String> label, ActionAjaxButton button) {
            this.style = style;
            this.label = label;
            this.button = button;
        }
    }
}
