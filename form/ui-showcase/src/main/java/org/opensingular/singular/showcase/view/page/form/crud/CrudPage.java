/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.view.page.form.crud;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

import org.opensingular.singular.showcase.view.template.Content;
import org.opensingular.singular.showcase.view.template.Template;

@MountPath("form/crud")
@SuppressWarnings("serial")
public class CrudPage extends Template {
    public static final String TYPE_NAME = "type";
    @Override
    protected Content getContent(String id) {
        StringValue type = getPageParameters().get(TYPE_NAME);
        return new CrudContent(id, type);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemDemo').addClass('active');"));
    }
}
