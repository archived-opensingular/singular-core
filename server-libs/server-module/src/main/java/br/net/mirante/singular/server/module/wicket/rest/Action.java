/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.module.wicket.rest;

public class Action {

    private String name;
    private Long flowCod;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFlowCod() {
        return flowCod;
    }

    public void setFlowCod(Long flowCod) {
        this.flowCod = flowCod;
    }
}
