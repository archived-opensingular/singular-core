/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import org.opensingular.flow.core.entity.IEntityExecutionVariable;
import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.entity.IEntityVariableType;

/**
 * The base persistent class for the TB_VARIAVEL_EXECUCAO_TRANSICAO database
 * table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractExecutionVariableEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractExecutionVariableEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <PROCESS_INSTANCE>
 * @param <TASK_INSTANCE>
 * @param <VAR_INSTANCE>
 * @param <VAR_TYPE>
 */
@MappedSuperclass
@Table(name = "TB_VARIAVEL_EXECUCAO_TRANSICAO")
public abstract class AbstractExecutionVariableEntity<PROCESS_INSTANCE extends IEntityProcessInstance, TASK_INSTANCE extends IEntityTaskInstance, VAR_INSTANCE extends IEntityVariableInstance, VAR_TYPE extends IEntityVariableType> extends BaseEntity<Integer> implements IEntityExecutionVariable {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_VARIAVEL_EXECUCAO_TRANSICAO";

    @Id
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    @Column(name = "CO_VARIAVEL_EXECUCAO_TRANSICAO")
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO", nullable = false, updatable = false)
    private PROCESS_INSTANCE processInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VARIAVEL")
    private VAR_INSTANCE variable;

    @Column(name = "NO_VARIAVEL", nullable = false, updatable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_TAREFA_ORIGEM", nullable = true, updatable = false)
    private TASK_INSTANCE originTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_TAREFA_DESTINO", nullable = true, updatable = false)
    private TASK_INSTANCE destinationTask;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_HISTORICO", nullable = false, updatable = false)
    private Date date;

    @Column(name = "VL_NOVO", nullable = false, length = 1000)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_TIPO_VARIAVEL", nullable = false)
    private VAR_TYPE type;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public PROCESS_INSTANCE getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(PROCESS_INSTANCE processInstance) {
        this.processInstance = processInstance;
    }

    public VAR_INSTANCE getVariable() {
        return variable;
    }

    public void setVariable(VAR_INSTANCE variable) {
        this.variable = variable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public TASK_INSTANCE getOriginTask() {
        return originTask;
    }

    public void setOriginTask(TASK_INSTANCE originTask) {
        this.originTask = originTask;
    }

    @Override
    public TASK_INSTANCE getDestinationTask() {
        return destinationTask;
    }

    public void setDestinationTask(TASK_INSTANCE destinationTask) {
        this.destinationTask = destinationTask;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    public VAR_TYPE getType() {
        return type;
    }

    public void setType(VAR_TYPE type) {
        this.type = type;
    }

}
