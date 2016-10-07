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

package org.opensingular.form.wicket.renderer;

import org.opensingular.form.SInstance;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.List;


public class SingularChoiceRenderer implements IChoiceRenderer<Serializable> {

    private static final long serialVersionUID = -4297329829600866968L;

    private final IModel<? extends SInstance> model;

    public SingularChoiceRenderer(IModel<? extends SInstance> model) {
        this.model = model;
    }

    @Override
    public String getDisplayValue(Serializable val) {
        return String.valueOf(model.getObject().asAtrProvider().getDisplayFunction().apply(val));
    }

    @Override
    public String getIdValue(Serializable val, int index) {
        return String.valueOf(model.getObject().asAtrProvider().getIdFunction().apply(val));
    }

    @Override
    public Serializable getObject(String id, IModel<? extends List<? extends Serializable>> choices) {
        return choices
                .getObject()
                .stream()
                .filter(choice -> getIdValue(choice, choices.getObject().indexOf(choice)).equals(id)).findFirst().orElse(null);
    }

}
