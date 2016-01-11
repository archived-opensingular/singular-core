package br.net.mirante.singular.bamclient.portlet;

import java.io.Serializable;

public class PortletQuickFilter implements Serializable {

    private String label;
    private Serializable object;

    public PortletQuickFilter(String label, Serializable object) {
        this.label = label;
        this.object = object;
    }

    public String getLabel() {
        return label;
    }

    public Serializable getObject() {
        return object;
    }
}
