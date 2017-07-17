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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleAccessStrategy extends TaskAccessStrategy<FlowInstance> {

    public static RoleAccessStrategy of(SProcessRole processRole) {
        return new RoleAccessStrategy(processRole);
    }

    public static RoleAccessStrategy of(SProcessRole executionRole, SProcessRole visualizeRole) {
        return new RoleAccessStrategy(executionRole, visualizeRole);
    }

    private final SProcessRole executionRole;

    private final SProcessRole visualizeRole;

    protected RoleAccessStrategy(SProcessRole mPapelExecucao) {
        this(mPapelExecucao, null);
    }

    protected RoleAccessStrategy(SProcessRole mPapelExecucao, SProcessRole mPapelVisualizacao) {
        super();
        this.executionRole = mPapelExecucao;
        this.visualizeRole = mPapelVisualizacao;
    }

    public SProcessRole getPapelExecucao() {
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
        if (visualizeRole != null) {
            for (IEntityRoleInstance entityRole : instance.getUserRoles()) {
                if (isSameRole(visualizeRole, entityRole) && user.is(entityRole.getUser())) {
                    return true;
                }
            }
        }
        return canExecute(instance, user);
    }

    private boolean isSameRole(SProcessRole processRole, IEntityRoleInstance entityRole) {
        return entityRole.getRole().getAbbreviation().equalsIgnoreCase(processRole.getAbbreviation());
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
    public List<SUser> listAllocableUsers(FlowInstance instance) {
        return instance.getUserRoles()
                .stream()
                .filter(entityRole -> isSameRole(executionRole, entityRole))
                .map(IEntityRoleInstance::getUser)
                .sorted()
                .collect(Collectors.toList());

    }

    @Override
    public List<String> getExecuteRoleNames(FlowDefinition<?> definicao, STask<?> task) {
        return Lists.newArrayList("Papel " + executionRole.getName());
    }

    @Override
    public List<String> getVisualizeRoleNames(FlowDefinition<?> definicao, STask<?> task) {
        if (visualizeRole == null) {
            return getExecuteRoleNames(definicao, task);
        }
        return Lists.newArrayList("Papel " + visualizeRole.getName());
    }

    @Override
    public AccessStrategyType getType() {
        return AccessStrategyType.E;
    }

    @Override
    public SUser getAutomaticAllocatedUser(FlowInstance instancia, TaskInstance tarefa) {
        IEntityRoleInstance role = instancia.getRoleUserByAbbreviation(executionRole.getName());
        return role != null ? role.getUser() : null;
    }
}
