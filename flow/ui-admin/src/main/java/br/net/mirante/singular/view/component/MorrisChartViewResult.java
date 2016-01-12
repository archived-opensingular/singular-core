package br.net.mirante.singular.view.component;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.net.mirante.singular.bamclient.portlet.MorrisChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;

public class MorrisChartViewResult extends AbstractChartViewResult<MorrisChartPortletConfig> {

    public MorrisChartViewResult(String id, IModel<MorrisChartPortletConfig> config, IModel<PortletContext> context) {
        super(id, config, context);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        final PackageResourceReference prr = new PackageResourceReference(AmChartViewResult.class, "MorrisChartViewResult.js");

        final String template = "MorrisChartViewResult.createChart('%s', %s, %s)";
        final String chartScript = String.format(template, this.getMarkupId(true),
                getConfig().getObject().getChart().getDefinition(), getSerializedContext());

        response.render(JavaScriptHeaderItem.forReference(prr));
        response.render(OnDomReadyHeaderItem.forScript(chartScript));
    }

}
