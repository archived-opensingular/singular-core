/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.opensingular.singular.flow.core.entity.AccessStrategyType;
import org.opensingular.singular.flow.core.entity.IEntityRoleInstance;

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
