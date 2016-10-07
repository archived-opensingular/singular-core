/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view.component;


import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.model.IModel;

import com.opensingular.bam.client.portlet.AmChartPortletConfig;
import com.opensingular.bam.client.portlet.MorrisChartPortletConfig;
import com.opensingular.bam.client.portlet.PortletConfig;
import com.opensingular.bam.client.portlet.PortletContext;

public class PortletViewConfigResolver {

    private static Map<Class<? extends PortletConfig>, ViewResultFactory> map;

    static {
        map = new HashMap<>();
        map.put(AmChartPortletConfig.class, new ViewResultFactory<AmChartPortletConfig>() {
            @Override
            public AmChartViewResultPanel create(String id, IModel<AmChartPortletConfig> config, IModel<PortletContext> context) {
                return new AmChartViewResultPanel(id, config, context);
            }
        });
        map.put(MorrisChartPortletConfig.class, new ViewResultFactory<MorrisChartPortletConfig>() {
            @Override
            public MorrisChartViewResultPanel create(String id, IModel<MorrisChartPortletConfig> config, IModel<PortletContext> context) {
                return new MorrisChartViewResultPanel(id, config, context);
            }
        });
    }

    public static <C extends PortletConfig<C>> ViewResultPanel newViewResult(String id, IModel<C> config, IModel<PortletContext> context) {
        return map.get(config.getObject().getClass()).create(id, config, context);
    }

}
