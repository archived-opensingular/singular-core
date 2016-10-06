/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.bam.wicket.view.page.processo;

import org.opensingular.singular.bam.wicket.view.template.Template;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

import org.opensingular.singular.bam.wicket.view.template.Content;

@MountPath("process/metadata")
public class MetadadosPage extends Template {

    @Override
    protected Content getContent(String id) {
        StringValue processDefinitionCode = getPageParameters().get(Content.PROCESS_DEFINITION_COD_PARAM);
        return new MetadadosContent(id, withSideBar(), processDefinitionCode.toString());
    }

    @Override
    protected boolean withSideBar() {
        return false;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        StringBuilder script = new StringBuilder();
        script.append("$('#_menuSubFlow').addClass('open');")
                .append("$('#_menuSubFlow>a>span.arrow').addClass('open');")
                .append("$('#_menuSubFlow>ul').show();")
                .append("$('#_menuItemFlowProcess').addClass('active');");
        response.render(OnDomReadyHeaderItem.forScript(script));
    }
}
