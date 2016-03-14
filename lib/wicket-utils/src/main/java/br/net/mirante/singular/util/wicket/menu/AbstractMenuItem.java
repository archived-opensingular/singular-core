/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.menu;

import br.net.mirante.singular.util.wicket.resource.Icone;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class AbstractMenuItem extends Panel {

    protected String title;
    protected Icone icon;

    public AbstractMenuItem(String id) {
        super(id);
    }

    protected abstract boolean configureActiveItem();
}
