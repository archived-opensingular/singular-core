package br.net.mirante.singular.view.component;


import java.util.HashMap;
import java.util.Map;

import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletConfig;

public class PortletViewConfigResolver {

    private static Map<Class<? extends PortletConfig>, ViewResultFactory> map;

    static {
        map = new HashMap<>();
        map.put(AmChartPortletConfig.class, new ViewResultFactory<AmChartViewResult, AmChartPortletConfig>() {
            @Override
            public AmChartViewResult create(String id, AmChartPortletConfig config) {
                return new AmChartViewResult(id, config);
            }
        });
    }

    public static <C extends PortletConfig> ViewResult newViewResult(String id, C config) {
        return map.get(config.getClass()).create(id, config);
    }

}
