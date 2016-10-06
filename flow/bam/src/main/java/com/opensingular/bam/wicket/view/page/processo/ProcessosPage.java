/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view.page.processo;

import com.opensingular.bam.wicket.view.template.Content;
import com.opensingular.bam.wicket.view.template.Template;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("process")
public class ProcessosPage extends Template {

    @Override
    protected Content getContent(String id) {
        StringValue processDefinitionCode = getPageParameters().get(Content.PROCESS_DEFINITION_COD_PARAM);
        if (processDefinitionCode.isNull()) {
            return new ProcessosContent(id, withSideBar());
        } else {
            return new InstanciasContent(id, withSideBar(), processDefinitionCode.toString());
        }
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
