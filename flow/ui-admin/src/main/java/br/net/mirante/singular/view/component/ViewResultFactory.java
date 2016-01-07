package br.net.mirante.singular.view.component;

import br.net.mirante.singular.bamclient.portlet.PortletConfig;

public interface ViewResultFactory<VR extends ViewResult, C extends PortletConfig> {

    VR create(String id, C config);
}
