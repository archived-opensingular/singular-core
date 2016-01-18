package br.net.mirante.singular.view.component;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;

public class AmChartViewResultPanel extends AbstractChartViewResultPanel<AmChartPortletConfig> {

    public AmChartViewResultPanel(String id, IModel<AmChartPortletConfig> config, IModel<PortletContext> context) {
        super(id, config, context);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        final PackageResourceReference prr = new PackageResourceReference(AmChartViewResultPanel.class, getClass().getSimpleName()+".js");

        final String template = "AmChartViewResultPanel.createChart('%s', %s, %s)";
        final String chartScript = String.format(template, this.getMarkupId(true),
                getConfig().getObject().getChart().getDefinition(), getSerializedContext());

        response.render(JavaScriptHeaderItem.forReference(prr));
        response.render(OnDomReadyHeaderItem.forScript(chartScript));
    }

}
