/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.core;

import java.io.Serializable;
import java.util.Objects;

public final class SBusinessRole implements Serializable {

    private final String abbreviation;

    private final String name;

    private final boolean automaticUserAllocation;

    private final BusinessRoleStrategy<FlowInstance> businessRoleStrategy;

    @SuppressWarnings("unchecked")
    SBusinessRole(String name, String abbreviation, BusinessRoleStrategy<? extends FlowInstance> businessRoleStrategy, boolean automaticUserAllocation) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(abbreviation);
        Objects.requireNonNull(businessRoleStrategy);
        this.abbreviation = abbreviation;
        this.name = name;
        this.businessRoleStrategy = (BusinessRoleStrategy<FlowInstance>) businessRoleStrategy;
        this.automaticUserAllocation = automaticUserAllocation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    public BusinessRoleStrategy<FlowInstance> getBusinessRoleStrategy() {
        return businessRoleStrategy;
    }

    public boolean isAutomaticBusinessRoleAllocation() {
        return automaticUserAllocation;
    }

    @Override
    public String toString() {
        return "SProcessRole [abbreviation=" + abbreviation + ", name=" + name
                + ", automaticBusinessRoleAllocation=" + automaticUserAllocation
                + ", businessRoleSettingStrategy=" + businessRoleStrategy + "]";
    }
}
