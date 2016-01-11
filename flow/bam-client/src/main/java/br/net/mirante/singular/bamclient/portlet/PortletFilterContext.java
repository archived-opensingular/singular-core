package br.net.mirante.singular.bamclient.portlet;

import java.io.Serializable;

public class PortletFilterContext implements Serializable {

    private PortletQuickFilter quickFilter;

    public PortletQuickFilter getQuickFilter() {
        return quickFilter;
    }

    public void setQuickFilter(PortletQuickFilter quickFilter) {
        this.quickFilter = quickFilter;
    }
}
