/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.resource;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

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
