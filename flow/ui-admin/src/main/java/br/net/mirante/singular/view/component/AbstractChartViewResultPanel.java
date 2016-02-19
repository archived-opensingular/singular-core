package br.net.mirante.singular.view.component;

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
