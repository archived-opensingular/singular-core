/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.service.dto;

import java.io.Serializable;
import java.util.Map;

public class BoxItemAction implements Serializable {

    private String name;
    private String endpoint;
    private boolean useExecute = false;

    public BoxItemAction() {
    }

    public BoxItemAction(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.endpoint = (String) map.get("endpoint");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isUseExecute() {
        return useExecute;
    }

    public void setUseExecute(boolean useExecute) {
        this.useExecute = useExecute;
    }
}