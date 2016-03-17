/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.wicket.view.component;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.bamclient.portlet.ChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

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
