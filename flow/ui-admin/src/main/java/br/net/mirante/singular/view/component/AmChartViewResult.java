package br.net.mirante.singular.view.component;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletFilterContext;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

public class AmChartViewResult extends ViewResult<AmChartPortletConfig> {

    public AmChartViewResult(String id, IModel<AmChartPortletConfig> config, IModel<PortletFilterContext> filter) {
        super(id, config, filter);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final String chartScriptTemplate = "AmCharts.makeChart('%s', %s)";
        final String chartScript = String.format(chartScriptTemplate, this.getMarkupId(true), getConfig().getObject().getChart().getDefinition(getFilter().getObject()));
        response.render(OnDomReadyHeaderItem.forScript(chartScript));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(WicketUtils.$b.classAppender("columnLine"));
    }

}
