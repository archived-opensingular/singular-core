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

package org.opensingular.flow.persistence.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OrderBy;
import org.opensingular.flow.core.SUser;
import org.opensingular.flow.core.entity.IEntityExecutionVariable;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

/**
 * The base persistent class for the flow instance database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name {@link AbstractFlowInstanceEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractFlowInstanceEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 * 
 * @param <USER>
 * @param <FLOW_VERSION>
 * @param <TASK_INSTANCE>
 * @param <VARIABLE_INSTANCE>
 * @param <USER_ROLE>
 * @param <EXECUTION_VAR>
 */
@MappedSuperclass
@SuppressWarnings("unchecked")
@Table(name = "TB_INSTANCIA_PROCESSO")
public abstract class AbstractFlowInstanceEntity<USER extends SUser, FLOW_VERSION extends IEntityFlowVersion, TASK_INSTANCE extends IEntityTaskInstance, VARIABLE_INSTANCE extends IEntityVariableInstance, USER_ROLE extends IEntityRoleInstance, EXECUTION_VAR extends IEntityExecutionVariable> extends BaseEntity<Integer> implements
        IEntityFlowInstance {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_INSTANCIA_PROCESSO";

    @Id
    @Column(name = "CO_INSTANCIA_PROCESSO")
    @GeneratedValue(generator = PK_GENERATOR_NAME, strategy = GenerationType.AUTO)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_PROCESSO", nullable = false, foreignKey = @ForeignKey(name = "FK_INST_PROCES_VERSAO_PROCESSO"))
    private FLOW_VERSION flowVersion;

    @Column(name = "DS_INSTANCIA_PROCESSO", length = 300)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ATOR_CRIADOR", foreignKey = @ForeignKey(name = "FK_INST_PROCES_ATOR_CRIADOR"))
    private USER userCreator;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INICIO", nullable = false)
    private Date beginDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIM")
    private Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_TAREFA_PAI", foreignKey = @ForeignKey(name = "FK_INST_PROCES_INST_TAR_PAI"))
    private TASK_INSTANCE parentTask;

    @OneToMany(mappedBy = "flowInstance", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<VARIABLE_INSTANCE> variables;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flowInstance", cascade = CascadeType.REMOVE)
    private List<USER_ROLE> roles;

    @OrderBy(clause = "DT_INICIO asc")
    @OneToMany(mappedBy = "flowInstance", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TASK_INSTANCE> tasks;

    @OrderBy(clause = "DT_HISTORICO asc")
    @OneToMany(mappedBy = "flowInstance", fetch = FetchType.LAZY)
    private List<EXECUTION_VAR> historicalVariables;

    @Override
    public USER_ROLE getRoleUserByAbbreviation(String roleAbbreviation) {
        return (USER_ROLE) IEntityFlowInstance.super.getRoleUserByAbbreviation(roleAbbreviation);
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
    public FLOW_VERSION getFlowVersion() {
        return flowVersion;
    }

    public void setFlowVersion(FLOW_VERSION flowVersion) {
        this.flowVersion = flowVersion;
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
    public List<USER_ROLE> getRoles() {
        return roles;
    }

    public void setRoles(List<USER_ROLE> roles) {
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
