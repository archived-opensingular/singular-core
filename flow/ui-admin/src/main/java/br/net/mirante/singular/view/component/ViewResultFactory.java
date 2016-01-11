package br.net.mirante.singular.view.component;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletFilterContext;

public interface ViewResultFactory<VR extends ViewResult, C extends PortletConfig> {

    VR create(String id, IModel<C> config, IModel<PortletFilterContext> filter);
}
