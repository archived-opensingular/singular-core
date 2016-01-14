package br.net.mirante.singular.view.component;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;

public interface ViewResultFactory<C extends PortletConfig> {

    ViewResultPanel create(String id, IModel<C> config, IModel<PortletContext> filter);
}
