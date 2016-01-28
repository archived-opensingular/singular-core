package br.net.mirante.singular.bamclient.portlet;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import br.net.mirante.singular.bamclient.util.SelfReference;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AmChartPortletConfig.class, name = "AmChartPortletConfig"),
        @JsonSubTypes.Type(value = MorrisChartPortletConfig.class, name = "MorrisChartPortletConfig")
})
public abstract class PortletConfig<T extends PortletConfig<T>> implements Serializable, SelfReference<T> {

    private PortletSize portletSize = PortletSize.MEDIUM;
    private String title;
    private String subtitle;
    private List<PortletQuickFilter> quickFilter = new ArrayList<>();
    private List<FilterConfig> filterConfigs = new ArrayList<>();
    private String filterClassName;

    public PortletConfig() {
    }

    public T setPortletSize(PortletSize portletSize) {
        this.portletSize = portletSize;
        return self();
    }

    public T setTitle(String title) {
        this.title = title;
        return self();
    }

    public T setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return self();
    }

    public T setQuickFilter(List<PortletQuickFilter> quickFilter) {
        this.quickFilter = quickFilter;
        return self();
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

    public List<PortletQuickFilter> getQuickFilter() {
        return quickFilter;
    }

    public List<FilterConfig> getFilterConfigs() {
        return filterConfigs;
    }

    public void setFilterConfigs(List<FilterConfig> filterConfigs) {
        this.filterConfigs = filterConfigs;
    }

    public String getFilterClassName() {
        return filterClassName;
    }

    public void setFilterClassName(String filterClassName) {
        this.filterClassName = filterClassName;
    }
}
