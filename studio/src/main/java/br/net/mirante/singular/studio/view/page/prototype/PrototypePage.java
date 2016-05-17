/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.view.page.prototype;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.studio.view.template.Content;
import br.net.mirante.singular.studio.view.template.Template;

@MountPath("prototype/edit")
public class PrototypePage extends Template {
    protected static String ID = "id";

    @Override
    protected Content getContent(String id) {
        StringValue idValue = getPageParameters().get(ID);
        return new PrototypeContent(id, idValue);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemPrototype').addClass('active');"));
    }
}
