package br.net.mirante.singular.bamclient.portlet;

import java.io.Serializable;
import java.util.Set;

import com.google.gson.Gson;

import br.net.mirante.singular.bamclient.portlet.filter.SingularPortletFilter;

public class PortletContext implements Serializable {

    private DataEndpoint dataEndpoint;
    private PortletQuickFilter quickFilter;
    private String processDefinitionCode;
    private Set<String> processDefinitionKeysWithAccess;
    private Integer portletIndex;
    private String serializedJSONFilter;
    private String filterClassName;

    public PortletQuickFilter getQuickFilter() {
        return quickFilter;
    }

    public void setQuickFilter(PortletQuickFilter quickFilter) {
        this.quickFilter = quickFilter;
    }

    public DataEndpoint getDataEndpoint() {
        return dataEndpoint;
    }

    public void setDataEndpoint(DataEndpoint dataEndpoint) {
        this.dataEndpoint = dataEndpoint;
    }

    public String getProcessDefinitionCode() {
        return processDefinitionCode;
    }

    public void setProcessDefinitionCode(String processDefinitionCode) {
        this.processDefinitionCode = processDefinitionCode;
    }

    public Set<String> getProcessDefinitionKeysWithAccess() {
        return processDefinitionKeysWithAccess;
    }

    public void setProcessDefinitionKeysWithAccess(Set<String> processDefinitionKeysWithAccess) {
        this.processDefinitionKeysWithAccess = processDefinitionKeysWithAccess;
    }

    public Integer getPortletIndex() {
        return portletIndex;
    }

    public void setPortletIndex(Integer portletIndex) {
        this.portletIndex = portletIndex;
    }

    public <T extends SingularPortletFilter> T loadFilter() {
        if (filterClassName != null) {
            try {
                Class clazz = Class.forName(filterClassName);
                if (SingularPortletFilter.class.isAssignableFrom(clazz)) {
                    Gson gson = new Gson();
                    return (T) gson.fromJson(serializedJSONFilter, clazz);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getSerializedJSONFilter() {
        return serializedJSONFilter;
    }

    public void setSerializedJSONFilter(String serializedJSONFilter) {
        this.serializedJSONFilter = serializedJSONFilter;
    }

    public String getFilterClassName() {
        return filterClassName;
    }

    public void setFilterClassName(String filterClassName) {
        this.filterClassName = filterClassName;
    }
}
