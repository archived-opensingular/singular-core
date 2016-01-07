package br.net.mirante.singular.view.component;


import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.bamclient.portlet.PortletConfig;

public abstract class ViewResult<T extends PortletConfig> extends Panel {

    private T config;

    public ViewResult(String id, T config) {
        super(id);
        this.config = config;
    }

    public T getConfig() {
        return config;
    }
}
