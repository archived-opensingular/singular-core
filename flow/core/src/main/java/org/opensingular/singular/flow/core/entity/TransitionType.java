/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.entity;

public enum TransitionType {
    /**
     * Event
     */
    E("Event"), 
    /**
     * Automatic
     */
    A("Automatic"), 
    /**
     * Human
     */
    H("Human");
    private final String name;

    private TransitionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public boolean isEvent(){
        return equals(E);
    }

    public boolean isAutomatic(){
        return equals(A);
    }

    public boolean isHuman(){
        return equals(H);
    }
}
