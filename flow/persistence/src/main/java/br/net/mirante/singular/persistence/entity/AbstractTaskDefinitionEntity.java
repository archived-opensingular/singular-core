/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.flow.core.entity.AccessStrategyType;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityRoleTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;

/**
 * The base persistent class for the TB_DEFINICAO_TAREFA database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractTaskDefinitionEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractTaskDefinitionEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <PROCESS_DEF>
 * @param <TASK_VERSION>
 */
@MappedSuperclass
@Table(name = "TB_DEFINICAO_TAREFA")
public abstract class AbstractTaskDefinitionEntity<PROCESS_DEF extends IEntityProcessDefinition, TASK_VERSION extends IEntityTaskVersion, ROLE_TASK extends IEntityRoleTask> extends BaseEntity<Integer> implements IEntityTaskDefinition {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_DEFINICAO_TAREFA";

    @Id
    @Column(name = "CO_DEFINICAO_TAREFA")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO", nullable = false)
    private PROCESS_DEF processDefinition;

    @Column(name = "SG_TAREFA", length = 100, nullable = false)
    private String abbreviation;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taskDefinition")
    private List<TASK_VERSION> versions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_ESTRATEGIA_SEGURANCA", length = 1)
    private AccessStrategyType accessStrategyType;

    @OneToMany(mappedBy = "taskDefinition", fetch = FetchType.LAZY)
    private List<ROLE_TASK> rolesTask;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public PROCESS_DEF getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(PROCESS_DEF processDefinition) {
        this.processDefinition = processDefinition;
    }

    @Override
    public String getAbbreviation() {
        return abbreviation;
    }

    @Override
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public List<TASK_VERSION> getVersions() {
        return versions;
    }

    public void setVersions(List<TASK_VERSION> versions) {
        this.versions = versions;
    }

    @Override
    public AccessStrategyType getAccessStrategyType() {
        return accessStrategyType;
    }

    @Override
    public void setAccessStrategyType(AccessStrategyType accessStrategyType) {
        this.accessStrategyType = accessStrategyType;
    }

    @Override
    public List<ROLE_TASK> getRolesTask() {
        return rolesTask;
    }

    public void setRolesTask(List<ROLE_TASK> rolesTask) {
        this.rolesTask = rolesTask;
    }
}
