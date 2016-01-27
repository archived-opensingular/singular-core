package br.net.mirante.singular.bamclient.portlet;

import java.io.Serializable;
import java.util.Set;

public class PortletContext implements Serializable {

    private DataEndpoint dataEndpoint;
    private PortletQuickFilter quickFilter;
    private String processDefinitionCode;
    private Set<String> processDefinitionKeysWithAccess;
    private int portletIndex;

    public PortletContext(int portletIndex) {
        this.portletIndex = portletIndex;
    }

    public PortletContext() {
    }

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

    public int getPortletIndex() {
        return portletIndex;
    }

    public void setPortletIndex(int portletIndex) {
        this.portletIndex = portletIndex;
    }
}
