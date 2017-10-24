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

import com.google.common.collect.Lists;
import org.opensingular.flow.core.entity.AccessStrategyType;
import org.opensingular.flow.core.entity.IEntityRoleInstance;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleAccessStrategy extends TaskAccessStrategy<FlowInstance> {

    public static RoleAccessStrategy of(SBusinessRole businessRole) {
        return new RoleAccessStrategy(businessRole);
    }

    public static RoleAccessStrategy of(SBusinessRole executionRole, SBusinessRole visualizeRole) {
        return new RoleAccessStrategy(executionRole, visualizeRole);
    }

    private final SBusinessRole executionRole;

    private final SBusinessRole visualizationRole;

    protected RoleAccessStrategy(SBusinessRole executionRole) {
        this(executionRole, null);
    }

    protected RoleAccessStrategy(SBusinessRole executionRole, SBusinessRole visualizationRole) {
        super();
        this.executionRole = executionRole;
        this.visualizationRole = visualizationRole;
    }

    public SBusinessRole getExecutionRole() {
        return executionRole;
    }

    @Override
    public boolean canExecute(FlowInstance instance, SUser user) {
        for (IEntityRoleInstance entityRole : instance.getUserRoles()) {
            if (isSameRole(executionRole, entityRole) && user.is(entityRole.getUser())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canVisualize(FlowInstance instance, SUser user) {
        if (visualizationRole != null) {
            for (IEntityRoleInstance entityRole : instance.getUserRoles()) {
                if (isSameRole(visualizationRole, entityRole) && user.is(entityRole.getUser())) {
                    return true;
                }
            }
        }
        return canExecute(instance, user);
    }

    private boolean isSameRole(SBusinessRole businessRole, IEntityRoleInstance entityRole) {
        return entityRole.getRole().getAbbreviation().equalsIgnoreCase(businessRole.getAbbreviation());
    }

    @Override
    public Set<Integer> getFirstLevelUsersCodWithAccess(FlowInstance instance) {
        final Set<Integer> cods = new HashSet<>();
        for (IEntityRoleInstance entityRole : instance.getUserRoles()) {
            if (isSameRole(executionRole, entityRole)) {
                cods.add(entityRole.getUser().getCod());
            }
        }
        return cods;
    }

    @Override
    @Nonnull
    public List<SUser> listAllowedUsers(@Nonnull FlowInstance instance) {
        return instance.getUserRoles()
                .stream()
                .filter(entityRole -> isSameRole(executionRole, entityRole))
                .map(IEntityRoleInstance::getUser)
                .sorted()
                .collect(Collectors.toList());

    }

    @Override
    public List<String> getExecuteRoleNames(FlowDefinition<?> definition, STask<?> task) {
        return Lists.newArrayList("Papel " + executionRole.getName());
    }

    @Override
    public List<String> getVisualizeRoleNames(FlowDefinition<?> definition, STask<?> task) {
        if (visualizationRole == null) {
            return getExecuteRoleNames(definition, task);
        }
        return Lists.newArrayList("Papel " + visualizationRole.getName());
    }

    @Override
    public AccessStrategyType getType() {
        return AccessStrategyType.E;
    }

    @Override
    public SUser getAutomaticAllocatedUser(FlowInstance instance, TaskInstance task) {
        IEntityRoleInstance role = instance.getRoleUserByAbbreviation(executionRole.getAbbreviation());
        return role != null ? role.getUser() : null;
    }
}
