/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.service.dto;

import java.io.Serializable;

import br.net.mirante.singular.util.wicket.resource.Icone;

public class ItemAction implements Serializable {

    private String name;
    private boolean defaultAction;

    private String label;
    private Icone icon;
    private String endpointPattern;

    public ItemAction() {
    }

    public ItemAction(String name) {
        this.name = name;
        defaultAction = true;
    }

    public ItemAction(String name, String label, Icone icon, String endpointPattern) {
        this.name = name;
        this.label = label;
        this.icon = icon;
        this.endpointPattern = endpointPattern;
        defaultAction = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefaultAction() {
        return defaultAction;
    }

    public void setDefaultAction(boolean defaultAction) {
        this.defaultAction = defaultAction;
    }

    public String getEndpointPattern() {
        return endpointPattern;
    }

    public void setEndpointPattern(String endpointPattern) {
        this.endpointPattern = endpointPattern;
    }
}
