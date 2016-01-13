package br.net.mirante.singular.bamclient.portlet;

import java.io.Serializable;

public class PortletQuickFilter implements Serializable {

    private String label;
    private String value;

    public PortletQuickFilter() {
    }

    public PortletQuickFilter(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
