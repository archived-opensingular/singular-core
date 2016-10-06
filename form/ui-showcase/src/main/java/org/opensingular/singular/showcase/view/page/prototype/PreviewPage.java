/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.view.page.prototype;

import org.opensingular.form.SIComposite;
import org.opensingular.singular.form.wicket.model.SInstanceRootModel;
import org.opensingular.singular.showcase.view.template.Content;
import org.opensingular.singular.showcase.view.template.Template;
import org.apache.wicket.Page;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class PreviewPage extends Template {

    private SInstanceRootModel<SIComposite> model;
    private Page backpage;

    public PreviewPage(SInstanceRootModel<SIComposite> model, Page backpage){
        this.model = model;
        this.backpage = backpage;
    }

    @Override
    protected Content getContent(String id) {
        return new PreviewContent(id,model, backpage);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemPrototype').addClass('active');"));
    }
}
