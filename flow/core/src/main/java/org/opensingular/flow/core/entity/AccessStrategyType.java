/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.entity;

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
