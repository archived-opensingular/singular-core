/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.entity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskPermission;

/**
 * The base persistent class for the RL_PERMISSAO_TAREFA database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractTaskPermissionEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractTaskDefinitionEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <TASK_DEF>
 */
@MappedSuperclass
@Table(name = "RL_PERMISSAO_TAREFA")
public abstract class AbstractTaskPermissionEntity<TASK_DEF extends IEntityTaskDefinition> extends BaseEntity<Integer> implements IEntityTaskPermission {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_PERMISSAO_TAREFA";

    @Id
    @Column(name = "CO_PERMISSAO_TAREFA")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_TAREFA", nullable = false)
    private TASK_DEF taskDefinition;

    @Column(name = "CO_PERMISSAO", length = 500, nullable = false)
    private String permission;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public TASK_DEF getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TASK_DEF taskDefinition) {
        this.taskDefinition = taskDefinition;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
