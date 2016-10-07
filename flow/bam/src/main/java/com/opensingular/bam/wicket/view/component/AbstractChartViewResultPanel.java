/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view.component;

import org.apache.wicket.model.IModel;

import com.opensingular.bam.client.portlet.ChartPortletConfig;
import com.opensingular.bam.client.portlet.PortletContext;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public abstract class AbstractChartViewResultPanel<C extends ChartPortletConfig> extends ViewResultPanel<C> {

    public AbstractChartViewResultPanel(String id, IModel<C> config, IModel<PortletContext> context) {
        super(id, config, context);
        context.getObject().setDataEndpoint(config.getObject().getDataEndpoint());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(WicketUtils.$b.classAppender(" columnLine "));
    }
}
