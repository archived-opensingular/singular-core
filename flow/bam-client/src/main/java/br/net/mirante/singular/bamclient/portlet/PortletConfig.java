package br.net.mirante.singular.bamclient.portlet;


import java.io.Serializable;
import java.util.Map;


public abstract class PortletConfig implements Serializable {

    private PortletSize portletSize = PortletSize.MEDIUM;
    private String title;
    private String subtitle;

    public PortletConfig setPortletSize(PortletSize portletSize) {
        this.portletSize = portletSize;
        return this;
    }

    public PortletConfig setTitle(String title) {
        this.title = title;
        return this;
    }

    public PortletConfig setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public PortletSize getPortletSize() {
        return portletSize;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
    
}
