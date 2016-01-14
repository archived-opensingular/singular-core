package br.net.mirante.singular.bamclient.portlet;

import java.io.Serializable;
import java.util.Set;

public class PortletContext implements Serializable {

    private String restEndpoint;
    private PortletQuickFilter quickFilter;
    private String processDefinitionCode;
    private Set<String> processDefinitionKeysWithAccess;

    public PortletQuickFilter getQuickFilter() {
        return quickFilter;
    }

    public void setQuickFilter(PortletQuickFilter quickFilter) {
        this.quickFilter = quickFilter;
    }

    public String getRestEndpoint() {
        return restEndpoint;
    }

    public void setRestEndpoint(String restEndpoint) {
        this.restEndpoint = restEndpoint;
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
}
