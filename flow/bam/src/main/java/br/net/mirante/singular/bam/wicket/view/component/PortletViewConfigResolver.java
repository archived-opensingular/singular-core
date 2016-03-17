/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.wicket.view.component;


import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.MorrisChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;

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
