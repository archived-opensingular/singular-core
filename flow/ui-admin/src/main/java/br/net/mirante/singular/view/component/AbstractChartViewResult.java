package br.net.mirante.singular.view.component;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.bamclient.portlet.ChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

public abstract class AbstractChartViewResult<C extends ChartPortletConfig> extends ViewResult<C> {

    public AbstractChartViewResult(String id, IModel<C> config, IModel<PortletContext> context) {
        super(id, config, context);
        context.getObject().setRestEndpoint(config.getObject().getRestEndpointURL());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(WicketUtils.$b.classAppender(" columnLine "));
    }
}
