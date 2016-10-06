/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.persistence.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.opensingular.singular.support.persistence.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OrderBy;

import org.opensingular.singular.flow.core.MUser;
import org.opensingular.singular.flow.core.entity.IEntityExecutionVariable;
import org.opensingular.singular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.singular.flow.core.entity.IEntityProcessVersion;
import org.opensingular.singular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.singular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.singular.flow.core.entity.IEntityVariableInstance;

/**
 * The base persistent class for the TB_INSTANCIA_PROCESSO database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name {@link AbstractProcessInstanceEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractProcessInstanceEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 * 
 * @param <USER>
 * @param <PROCESS_VERSION>
 * @param <TASK_INSTANCE>
 * @param <VARIABLE_INSTANCE>
 * @param <ROLE_USER>
 * @param <EXECUTION_VAR>
 */
@MappedSuperclass
@SuppressWarnings("unchecked")
@Table(name = "TB_INSTANCIA_PROCESSO")
public abstract class AbstractProcessInstanceEntity<USER extends MUser, PROCESS_VERSION extends IEntityProcessVersion, TASK_INSTANCE extends IEntityTaskInstance, VARIABLE_INSTANCE extends IEntityVariableInstance, ROLE_USER extends IEntityRoleInstance, EXECUTION_VAR extends IEntityExecutionVariable> extends BaseEntity<Integer> implements IEntityProcessInstance {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_INSTANCIA_PROCESSO";

    @Id
    @Column(name = "CO_INSTANCIA_PROCESSO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_PROCESSO", nullable = false)
    private PROCESS_VERSION processVersion;

    @Column(name = "DS_INSTANCIA_PROCESSO", length = 250)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ATOR_CRIADOR")
    private USER userCreator;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INICIO", nullable = false)
    private Date beginDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIM")
    private Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_TAREFA_PAI")
    private TASK_INSTANCE parentTask;

    @OneToMany(mappedBy = "processInstance", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<VARIABLE_INSTANCE> variables;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "processInstance", cascade = CascadeType.REMOVE)
    private List<ROLE_USER> roles;

    @OrderBy(clause = "DT_INICIO asc")
    @OneToMany(mappedBy = "processInstance", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TASK_INSTANCE> tasks;

    @OrderBy(clause = "DT_HISTORICO asc")
    @OneToMany(mappedBy = "processInstance", fetch = FetchType.LAZY)
    private List<EXECUTION_VAR> historicalVariables;

    @Override
    public ROLE_USER getRoleUserByAbbreviation(String siglaPapel) {
        return (ROLE_USER) IEntityProcessInstance.super.getRoleUserByAbbreviation(siglaPapel);
    }

    @Override
    public void addTask(IEntityTaskInstance taskInstance) {
        if (getTasks() == null) {
            setTasks(new ArrayList<>());
        }

        getTasks().add((TASK_INSTANCE) taskInstance);
    }

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public PROCESS_VERSION getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(PROCESS_VERSION processVersion) {
        this.processVersion = processVersion;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public USER getUserCreator() {
        return userCreator;
    }

    public void setUserCreator(USER userCreator) {
        this.userCreator = userCreator;
    }

    @Override
    public Date getBeginDate() {
        return beginDate;
    }

    @Override
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public TASK_INSTANCE getParentTask() {
        return parentTask;
    }

    @Override
    public void setParentTask(IEntityTaskInstance parentTask) {
        this.parentTask = (TASK_INSTANCE) parentTask;
    }

    @Override
    public List<VARIABLE_INSTANCE> getVariables() {
        return variables;
    }

    public void setVariables(List<VARIABLE_INSTANCE> variables) {
        this.variables = variables;
    }

    @Override
    public List<ROLE_USER> getRoles() {
        return roles;
    }

    public void setRoles(List<ROLE_USER> roles) {
        this.roles = roles;
    }

    @Override
    public List<TASK_INSTANCE> getTasks() {
        return tasks;
    }

    public void setTasks(List<TASK_INSTANCE> tasks) {
        this.tasks = tasks;
    }

    @Override
    public List<EXECUTION_VAR> getHistoricalVariables() {
        return historicalVariables;
    }

    public void setHistoricalVariables(List<EXECUTION_VAR> historicalVariables) {
        this.historicalVariables = historicalVariables;
    }
}
