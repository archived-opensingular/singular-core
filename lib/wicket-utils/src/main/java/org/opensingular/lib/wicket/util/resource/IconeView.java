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

package org.opensingular.lib.wicket.util.resource;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class IconeView extends WebMarkupContainer {

    public IconeView(String id) {
        this(id, $m.ofValue(), null, null);
    }

    public IconeView(String id, IModel<Icone> model, IModel<String> style, IModel<String> styleClass) {
        super(id, model);

        add($b.classAppender($m.get(() -> {
            if (getModelObject() != null) {
                return getModelObject().getCssClass();
            }
            return false;
        }), $m.isNullOrEmpty(getModel())));

        if (style != null) {
            add($b.attrAppender("style", style, ";"));
        }

        if (styleClass != null) {
            add($b.classAppender(styleClass));
        }
    }

    public IconeView setIcone(Icone icone) {
        return setModelObject(icone);
    }
    public IconeView setModelObject(Icone icone) {
        setDefaultModelObject(icone);
        return this;
    }
    public Icone getIcone() {
        return getModelObject();
    }
    public Icone getModelObject() {
        return (Icone) getDefaultModelObject();
    }
    @SuppressWarnings("unchecked")
    public IModel<Icone> getModel() {
        return (IModel<Icone>) getDefaultModel();
    }
}
