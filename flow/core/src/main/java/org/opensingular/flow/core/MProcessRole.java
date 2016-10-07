/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.io.Serializable;
import java.util.Objects;

public final class MProcessRole implements Serializable {

    private final String abbreviation;

    private final String name;

    private final boolean automaticUserAllocation;

    private final UserRoleSettingStrategy<ProcessInstance> userRoleSettingStrategy;

    @SuppressWarnings("unchecked")
    MProcessRole(String name, String abbreviation, UserRoleSettingStrategy<? extends ProcessInstance> userRoleSettingStrategy, boolean automaticUserAllocation) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(abbreviation);
        Objects.requireNonNull(userRoleSettingStrategy);
        this.abbreviation = abbreviation;
        this.name = name;
        this.userRoleSettingStrategy = (UserRoleSettingStrategy<ProcessInstance>) userRoleSettingStrategy;
        this.automaticUserAllocation = automaticUserAllocation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    public UserRoleSettingStrategy<ProcessInstance> getUserRoleSettingStrategy() {
        return userRoleSettingStrategy;
    }

    public boolean isAutomaticUserAllocation() {
        return automaticUserAllocation;
    }

    @Override
    public String toString() {
        return "MProcessRole [abbreviation=" + abbreviation + ", name=" + name
                + ", automaticUserAllocation=" + automaticUserAllocation
                + ", userRoleSettingStrategy=" + userRoleSettingStrategy + "]";
    }
}
