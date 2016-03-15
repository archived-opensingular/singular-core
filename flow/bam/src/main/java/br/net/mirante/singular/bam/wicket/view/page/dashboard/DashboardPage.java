/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.wicket.view.page.dashboard;

import br.net.mirante.singular.bam.wicket.view.template.Content;
import br.net.mirante.singular.bam.wicket.view.template.Template;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("dashboard")
public class DashboardPage extends Template {

    @Override
    protected Content getContent(String id) {
        StringValue customDashboardCode = getPageParameters().get(Content.CUSTOM_DASHBOARD_COD_PARAM);
        if (!customDashboardCode.isNull()) {
            return new CustomDashboardContent(id, customDashboardCode.toString());
        } else {
            StringValue processDefinitionCode = getPageParameters().get(Content.PROCESS_DEFINITION_COD_PARAM);
            return new DashboardContent(id, processDefinitionCode.toString());
        }
    }

}
