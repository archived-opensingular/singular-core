package br.net.mirante.singular.view.component;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;

import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;

public class AmChartViewResult extends ViewResult<AmChartPortletConfig> {

    private final WebMarkupContainer chart = new WebMarkupContainer("chart");

    public AmChartViewResult(String id, AmChartPortletConfig config) {
        super(id, config);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final String chartScriptTemplate = "AmCharts.makeChart('%s', %s)";
        final String chartScript = String.format(chartScriptTemplate, chart.getMarkupId(true), getConfig().getChartDefinition());
        response.render(OnDomReadyHeaderItem.forScript(chartScript));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(chart);
    }

}
