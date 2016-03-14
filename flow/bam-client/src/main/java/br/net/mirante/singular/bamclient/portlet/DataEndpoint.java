/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.portlet;


import java.io.Serializable;

import br.net.mirante.singular.bamclient.enums.EndpointType;

public class DataEndpoint implements Serializable {

    private EndpointType endpointType;
    private String dashboardViewName;
    private String url;

    public String getDashboardViewName() {
        return dashboardViewName;
    }

    public void setDashboardViewName(String dashboardViewName) {
        this.dashboardViewName = dashboardViewName;
    }

    public EndpointType getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(EndpointType endpointType) {
        this.endpointType = endpointType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @deprecated remover na proxima versao
     */
    @Deprecated
    public static DataEndpoint rest(String processAbbreviation, String dashboardViewName) {
        return rest(dashboardViewName);
    }

    public static DataEndpoint rest(String dashboardViewName) {
        final DataEndpoint endpoint = new DataEndpoint();
        endpoint.endpointType = EndpointType.REST;
        endpoint.dashboardViewName = dashboardViewName;
        return endpoint;
    }

    public static DataEndpoint local(String url) {
        final DataEndpoint endpoint = new DataEndpoint();
        endpoint.endpointType = EndpointType.LOCAL;
        endpoint.url = url;
        return endpoint;
    }
}
