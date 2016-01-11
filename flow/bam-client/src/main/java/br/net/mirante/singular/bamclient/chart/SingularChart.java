package br.net.mirante.singular.bamclient.chart;


import java.io.Serializable;

import br.net.mirante.singular.bamclient.portlet.PortletFilterContext;

public interface SingularChart extends Serializable {

    String getDefinition(PortletFilterContext filterContext);
}
