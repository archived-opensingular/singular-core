/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.view.page.prototype;

import org.apache.wicket.Page;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.studio.view.template.Content;
import br.net.mirante.singular.studio.view.template.Template;

public class PreviewPage extends Template {

    private MInstanceRootModel<SIComposite> model;
    private Page backpage;

    public PreviewPage(MInstanceRootModel<SIComposite>  model, Page backpage){
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
