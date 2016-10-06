/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.opensingular.singular.support.persistence.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.entity.IEntityTaskHistoricType;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstanceHistory;

/**
 * The base persistent class for the TB_HISTORICO_INSTANCIA_TAREFA database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name {@link AbstractTaskInstanceHistoryEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractTaskInstanceHistoryEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <USER>
 * @param <TASK_INSTANCE>
 * @param <TASK_HISTORIC_TYPE>
 */
@MappedSuperclass
@Table(name = "TB_HISTORICO_INSTANCIA_TAREFA")
public abstract class AbstractTaskInstanceHistoryEntity<USER extends MUser, TASK_INSTANCE extends IEntityTaskInstance, TASK_HISTORIC_TYPE extends IEntityTaskHistoricType> extends BaseEntity<Integer> implements IEntityTaskInstanceHistory {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_HISTORICO_ALOCACAO";

    @Id
    @Column(name = "CO_HISTORICO_ALOCACAO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_TAREFA", nullable = false)
    private TASK_INSTANCE taskInstance;

    @Column(name = "DT_INICIO_ALOCACAO", nullable = false, updatable = false)
    private Date beginDateAllocation;

    @Column(name = "DT_FIM_ALOCACAO")
    private Date endDateAllocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_TIPO_HISTORICO_TAREFA", nullable = false)
    private TASK_HISTORIC_TYPE type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ATOR_ALOCADO")
    private USER allocatedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ATOR_ALOCADOR")
    private USER allocatorUser;

    @Column(name = "DS_COMPLEMENTO")
    private String description;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public TASK_INSTANCE getTaskInstance() {
        return taskInstance;
    }

    public void setTaskInstance(TASK_INSTANCE taskInstance) {
        this.taskInstance = taskInstance;
    }

    @Override
    public Date getBeginDateAllocation() {
        return beginDateAllocation;
    }

    @Override
    public void setBeginDateAllocation(Date beginDateAllocation) {
        this.beginDateAllocation = beginDateAllocation;
    }

    @Override
    public Date getEndDateAllocation() {
        return endDateAllocation;
    }

    @Override
    public void setEndDateAllocation(Date endDateAllocation) {
        this.endDateAllocation = endDateAllocation;
    }

    @Override
    public TASK_HISTORIC_TYPE getType() {
        return type;
    }

    public void setType(TASK_HISTORIC_TYPE type) {
        this.type = type;
    }

    @Override
    public USER getAllocatedUser() {
        return allocatedUser;
    }

    public void setAllocatedUser(USER allocatedUser) {
        this.allocatedUser = allocatedUser;
    }

    @Override
    public USER getAllocatorUser() {
        return allocatorUser;
    }

    public void setAllocatorUser(USER allocatorUser) {
        this.allocatorUser = allocatorUser;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

}
