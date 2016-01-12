package br.net.mirante.singular.view.component;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

public class AmChartViewResult extends ViewResult<AmChartPortletConfig> {

    public AmChartViewResult(String id, IModel<AmChartPortletConfig> config, IModel<PortletContext> context) {
        super(id, config, context);
        context.getObject().setRestEndpoint(config.getObject().getRestEndpointURL());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        final PackageResourceReference prr = new PackageResourceReference(AmChartViewResult.class, "AmChartViewResult.js");

        final String template = "AmChartViewResult.createChart('%s', %s, %s)";
        final String chartScript = String.format(template, this.getMarkupId(true),
                getConfig().getObject().getChart().getDefinition(), getSerializedContext());

        response.render(JavaScriptHeaderItem.forReference(prr));
        response.render(OnDomReadyHeaderItem.forScript(chartScript));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(WicketUtils.$b.classAppender("columnLine"));
    }

}
