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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OrderBy;
import org.opensingular.flow.core.SUser;
import org.opensingular.flow.core.entity.IEntityExecutionVariable;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstanceHistory;
import org.opensingular.flow.core.entity.IEntityTaskTransitionVersion;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

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
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The base persistent class for the TB_INSTANCIA_TAREFA database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name {@link AbstractTaskInstanceEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractTaskInstanceEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <USER>
 * @param <FLOW_INSTANCE>
 * @param <TASK_VERSION>
 * @param <TASK_TRANSITION_VERSION>
 * @param <EXECUTION_VARIABLE>
 * @param <TASK_HISTORY>
 */
@MappedSuperclass
@Table(name = "TB_INSTANCIA_TAREFA")
public abstract class AbstractTaskInstanceEntity<USER extends SUser, FLOW_INSTANCE extends IEntityFlowInstance, TASK_VERSION extends IEntityTaskVersion, TASK_TRANSITION_VERSION extends IEntityTaskTransitionVersion, EXECUTION_VARIABLE extends IEntityExecutionVariable, TASK_HISTORY extends IEntityTaskInstanceHistory> extends BaseEntity<Integer> implements IEntityTaskInstance {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_INSTANCIA_TAREFA";

    @Id
    @Column(name = "CO_INSTANCIA_TAREFA")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO", nullable = false)
    private FLOW_INSTANCE flowInstance;

    @Column(name = "DT_INICIO", nullable = false, updatable = false)
    private Date beginDate;

    @Column(name = "DT_FIM")
    private Date endDate;

    @Column(name = "DT_ESPERADA_FIM")
    private Date targetEndDate;

    @Version
    @Column(name = "NU_VERSAO")
    private Integer versionStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_TAREFA", nullable = false, updatable = false)
    private TASK_VERSION task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ATOR_ALOCADO")
    private USER allocatedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ATOR_CONCLUSAO")
    private USER responsibleUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_TRANSICAO_EXECUTADA")
    private TASK_TRANSITION_VERSION executedTransition;

    @OrderBy(clause = "DT_INICIO_ALOCACAO")
    @OneToMany(mappedBy = "taskInstance", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<TASK_HISTORY> taskHistory = new ArrayList<>();

    @OrderBy(clause = "DT_HISTORICO")
    @OneToMany(mappedBy = "destinationTask", fetch = FetchType.LAZY)
    private List<EXECUTION_VARIABLE> inputVariables = new ArrayList<>();

    @OrderBy(clause = "DT_HISTORICO")
    @OneToMany(mappedBy = "originTask", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<EXECUTION_VARIABLE> outputVariables = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentTask", cascade = CascadeType.REMOVE)
    private List<FLOW_INSTANCE> childProcesses = new ArrayList<>();

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public FLOW_INSTANCE getFlowInstance() {
        return flowInstance;
    }

    public void setFlowInstance(FLOW_INSTANCE flowInstance) {
        this.flowInstance = flowInstance;
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
    public Date getTargetEndDate() {
        return targetEndDate;
    }

    @Override
    public void setTargetEndDate(Date targetEndDate) {
        this.targetEndDate = targetEndDate;
    }

    @Override
    public TASK_VERSION getTaskVersion() {
        return task;
    }

    public void setTask(TASK_VERSION task) {
        this.task = task;
    }

    @Override
    public USER getAllocatedUser() {
        return allocatedUser;
    }

    @Override
    public void setAllocatedUser(SUser allocatedUser) {
        this.allocatedUser = (USER) allocatedUser;
    }

    @Override
    public USER getResponsibleUser() {
        return responsibleUser;
    }

    @Override
    public void setResponsibleUser(SUser responsibleUser) {
        this.responsibleUser = (USER) responsibleUser;
    }

    @Override
    public TASK_TRANSITION_VERSION getExecutedTransition() {
        return executedTransition;
    }

    @Override
    public void setExecutedTransition(IEntityTaskTransitionVersion executedTransition) {
        this.executedTransition = (TASK_TRANSITION_VERSION) executedTransition;
    }

    @Override
    public List<TASK_HISTORY> getTaskHistory() {
        return taskHistory;
    }

    public void setTaskHistory(List<TASK_HISTORY> taskHistory) {
        this.taskHistory = taskHistory;
    }

    @Override
    public List<EXECUTION_VARIABLE> getInputVariables() {
        return inputVariables;
    }

    public void setInputVariables(List<EXECUTION_VARIABLE> inputVariables) {
        this.inputVariables = inputVariables;
    }

    @Override
    public List<EXECUTION_VARIABLE> getOutputVariables() {
        return outputVariables;
    }

    public void setOutputVariables(List<EXECUTION_VARIABLE> outputVariables) {
        this.outputVariables = outputVariables;
    }

    @Override
    public List<FLOW_INSTANCE> getChildProcesses() {
        return childProcesses;
    }

    public void setChildProcesses(List<FLOW_INSTANCE> childProcesses) {
        this.childProcesses = childProcesses;
    }

    @Override
    public void setVersionStamp(Integer versionStamp) {
        this.versionStamp = versionStamp;
    }

    @Override
    public Integer getVersionStamp() {
        return versionStamp;
    }

}
