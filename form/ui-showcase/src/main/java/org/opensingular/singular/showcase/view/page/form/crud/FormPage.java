/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.view.page.form.crud;

import org.opensingular.singular.showcase.view.template.Content;
import org.opensingular.singular.showcase.view.template.Template;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("form/edit")
@SuppressWarnings("serial")
public class FormPage extends Template {

    protected static final String TYPE_NAME  = "type";
    protected static final String MODEL_ID   = "id";
    protected static final String VIEW_MODE  = "viewMode";
    protected static final String ANNOTATION = "annotation";

    @Override
    protected Content getContent(String id) {
        StringValue type = getPageParameters().get(TYPE_NAME);
        StringValue idExampleData = getPageParameters().get(MODEL_ID);
        StringValue viewMode = getPageParameters().get(VIEW_MODE);
        StringValue annotation = getPageParameters().get(ANNOTATION);

        return new FormContent(id, type, idExampleData, viewMode, annotation);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemDemo').addClass('active');"));
    }

}
