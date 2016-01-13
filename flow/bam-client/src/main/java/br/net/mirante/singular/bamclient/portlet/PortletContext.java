package br.net.mirante.singular.bamclient.portlet;

import java.io.Serializable;

public class PortletContext implements Serializable {

    private String restEndpoint;
    private PortletQuickFilter quickFilter;
    private String processDefinitionCode;

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
}
