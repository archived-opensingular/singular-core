package br.net.mirante.singular.view.component;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;

import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

public class AmChartViewResult extends ViewResult<AmChartPortletConfig> {

    public AmChartViewResult(String id, AmChartPortletConfig config) {
        super(id, config);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final String chartScriptTemplate = "AmCharts.makeChart('%s', %s)";
        final String chartScript = String.format(chartScriptTemplate, this.getMarkupId(true), getConfig().getChartDefinition());
        response.render(OnDomReadyHeaderItem.forScript(chartScript));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(WicketUtils.$b.classAppender("columnLine"));
    }

}
