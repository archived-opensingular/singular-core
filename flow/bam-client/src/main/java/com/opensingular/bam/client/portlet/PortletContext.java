/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.portlet;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.opensingular.flow.core.DashboardContext;
import org.opensingular.flow.core.DashboardFilter;

public class PortletContext implements DashboardContext {

    private final static Logger LOGGER = LoggerFactory.getLogger(PortletContext.class);

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

    public <T extends DashboardFilter> T loadFilter() {
        if (filterClassName != null) {
            try {
                Class clazz = Class.forName(filterClassName);
                if (DashboardFilter.class.isAssignableFrom(clazz)) {
                    final GsonBuilder gsonBuilder  = new GsonBuilder().setDateFormat("dd/MM/yyyy");
                    final Gson gson = gsonBuilder.create();
                    return (T) gson.fromJson(serializedJSONFilter, clazz);
                }
            } catch (ClassNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
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
