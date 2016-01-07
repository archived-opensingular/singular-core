package br.net.mirante.singular.view.component;

import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.bamclient.portlet.PortletConfig;

public class PortletView<C extends PortletConfig> extends Panel {

    private final C config;

    public PortletView(String id, C config) {
        super(id);
        this.config = config;
    }

    protected ViewResult buildViewResult() {
        return PortletViewConfigResolver.newViewResult("portletContent", config);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildViewResult());
    }
}
