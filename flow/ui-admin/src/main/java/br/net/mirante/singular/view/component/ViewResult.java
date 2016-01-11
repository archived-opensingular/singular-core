package br.net.mirante.singular.view.component;


import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletFilterContext;

public abstract class ViewResult<T extends PortletConfig> extends Panel {

    private IModel<T> config;

    private IModel<PortletFilterContext> filter;

    public ViewResult(String id, IModel<T> config, IModel<PortletFilterContext> filter) {
        super(id);
        this.config = config;
        this.filter = filter;
    }

    public IModel<T> getConfig() {
        return config;
    }

    public IModel<PortletFilterContext> getFilter() {
        return filter;
    }

}

