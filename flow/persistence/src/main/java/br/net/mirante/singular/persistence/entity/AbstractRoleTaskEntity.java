/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.entity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.flow.core.entity.IEntityRoleDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityRoleTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.support.persistence.util.Constants;

/**
 * The base persistent class for the RL_PAPEL_TAREFA database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractRoleTaskEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractRoleTaskEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <TASK_DEF>
 * @param <PROCESS_ROLE>
 */
@MappedSuperclass
@Table(name = "RL_PAPEL_TAREFA", schema = Constants.SCHEMA)
public abstract class AbstractRoleTaskEntity<TASK_DEF extends IEntityTaskDefinition, PROCESS_ROLE extends IEntityRoleDefinition> extends BaseEntity<Integer> implements IEntityRoleTask {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_PAPEL_TAREFA";

    @Id
    @Column(name = "CO_PAPEL_TAREFA")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_PAPEL", nullable = false)
    private PROCESS_ROLE roleDefinition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_TAREFA", nullable = false)
    private TASK_DEF taskDefinition;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public PROCESS_ROLE getRoleDefinition() {
        return roleDefinition;
    }

    public void setRoleDefinition(PROCESS_ROLE roleDefinition) {
        this.roleDefinition = roleDefinition;
    }

    @Override
    public TASK_DEF getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TASK_DEF taskDefinition) {
        this.taskDefinition = taskDefinition;
    }
}
