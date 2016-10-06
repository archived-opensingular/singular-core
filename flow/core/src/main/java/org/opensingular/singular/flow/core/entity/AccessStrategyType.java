/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.entity;

public enum AccessStrategyType {

    D("Dynamic"),
    E("Static");

    private final String name;

    AccessStrategyType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
