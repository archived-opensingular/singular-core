package br.net.mirante.singular.view.component;


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
        map.put(AmChartPortletConfig.class, new ViewResultFactory<AmChartViewResult, AmChartPortletConfig>() {
            @Override
            public AmChartViewResult create(String id, IModel<AmChartPortletConfig> config, IModel<PortletContext> context) {
                return new AmChartViewResult(id, config, context);
            }
        });
        map.put(MorrisChartPortletConfig.class, new ViewResultFactory<MorrisChartViewResult, MorrisChartPortletConfig>() {
            @Override
            public MorrisChartViewResult create(String id, IModel<MorrisChartPortletConfig> config, IModel<PortletContext> context) {
                return new MorrisChartViewResult(id, config, context);
            }
        });
    }

    public static <C extends PortletConfig> ViewResult newViewResult(String id, IModel<C> config, IModel<PortletContext> context) {
        return map.get(config.getObject().getClass()).create(id, config, context);
    }

}
