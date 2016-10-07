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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.opensingular.flow.core.entity.AccessStrategyType;
import org.opensingular.flow.core.entity.IEntityRoleInstance;

public class RoleAccessStrategy extends TaskAccessStrategy<ProcessInstance> {

    public static RoleAccessStrategy of(MProcessRole processRole) {
        return new RoleAccessStrategy(processRole);
    }

    public static RoleAccessStrategy of(MProcessRole executionRole, MProcessRole visualizeRole) {
        return new RoleAccessStrategy(executionRole, visualizeRole);
    }

    private final MProcessRole executionRole;

    private final MProcessRole visualizeRole;

    protected RoleAccessStrategy(MProcessRole mPapelExecucao) {
        this(mPapelExecucao, null);
    }

    protected RoleAccessStrategy(MProcessRole mPapelExecucao, MProcessRole mPapelVisualizacao) {
        super();
        this.executionRole = mPapelExecucao;
        this.visualizeRole = mPapelVisualizacao;
    }

    public MProcessRole getPapelExecucao() {
        return executionRole;
    }

    @Override
    public boolean canExecute(ProcessInstance instance, MUser user) {
        for (IEntityRoleInstance entityRole : instance.getUserRoles()) {
            if (isSameRole(executionRole, entityRole) && user.is(entityRole.getUser())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canVisualize(ProcessInstance instance, MUser user) {
        if (visualizeRole != null) {
            for (IEntityRoleInstance entityRole : instance.getUserRoles()) {
                if (isSameRole(visualizeRole, entityRole) && user.is(entityRole.getUser())) {
                    return true;
                }
            }
        }
        return canExecute(instance, user);
    }

    private boolean isSameRole(MProcessRole processRole, IEntityRoleInstance entityRole) {
        return entityRole.getRole().getAbbreviation().equalsIgnoreCase(processRole.getAbbreviation());
    }

    @Override
    public Set<Integer> getFirstLevelUsersCodWithAccess(ProcessInstance instance) {
        final Set<Integer> cods = new HashSet<>();
        for (IEntityRoleInstance entityRole : instance.getUserRoles()) {
            if (isSameRole(executionRole, entityRole)) {
                cods.add(entityRole.getUser().getCod());
            }
        }
        return cods;
    }

    @Override
    public List<MUser> listAllocableUsers(ProcessInstance instance) {
        return instance.getUserRoles()
                .stream()
                .filter(entityRole -> isSameRole(executionRole, entityRole))
                .map(IEntityRoleInstance::getUser)
                .sorted()
                .collect(Collectors.toList());

    }

    @Override
    public List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, MTask<?> task) {
        return Lists.newArrayList("Papel " + executionRole.getName());
    }

    @Override
    public List<String> getVisualizeRoleNames(ProcessDefinition<?> definicao, MTask<?> task) {
        if (visualizeRole == null) {
            return getExecuteRoleNames(definicao, task);
        }
        return Lists.newArrayList("Papel " + visualizeRole.getName());
    }

    @Override
    public AccessStrategyType getType() {
        return AccessStrategyType.E;
    }
}
