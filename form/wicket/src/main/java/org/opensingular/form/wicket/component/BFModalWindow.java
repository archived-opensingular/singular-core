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

package org.opensingular.form.wicket.component;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.opensingular.lib.wicket.util.modal.BSModalWindow;

public class BFModalWindow extends BSModalWindow {

    public BFModalWindow(String id, boolean wrapBodyWithForm, boolean resetOnBodySwitch) {
        super(id, wrapBodyWithForm, resetOnBodySwitch);
    }
    public BFModalWindow(String id, boolean wrapBodyWithForm) {
        super(id, wrapBodyWithForm);
    }
    public BFModalWindow(String id, IModel<?> model, boolean wrapBodyWithForm, boolean resetOnBodySwitch) {
        super(id, model, wrapBodyWithForm, resetOnBodySwitch);
    }
    public BFModalWindow(String id, IModel<?> model, boolean wrapBodyWithForm) {
        super(id, model, wrapBodyWithForm);
    }
    public BFModalWindow(String id, IModel<?> model) {
        super(id, model);
    }
    public BFModalWindow(String id) {
        super(id);
    }

    @Override
    protected BSModalBorder newModalBorder(String id) {
        return new BFModalBorder(id);
    }
    
    @Override
    protected Form<?> newForm(String id) {
        return new SingularForm<>(id);
    }
}
