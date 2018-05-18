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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.opensingular.flow.core.entity.IEntityExecutionVariable;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.entity.IEntityVariableType;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

/**
 * The base persistent class for the TB_VARIAVEL_EXECUCAO_TRANSICAO database
 * table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractExecutionVariableEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractExecutionVariableEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <FLOW_INSTANCE>
 * @param <TASK_INSTANCE>
 * @param <VAR_INSTANCE>
 * @param <VAR_TYPE>
 */
@MappedSuperclass
@Table(name = "TB_VARIAVEL_EXECUCAO_TRANSICAO")
public abstract class AbstractExecutionVariableEntity<FLOW_INSTANCE extends IEntityFlowInstance, TASK_INSTANCE extends IEntityTaskInstance, VAR_INSTANCE extends IEntityVariableInstance, VAR_TYPE extends IEntityVariableType> extends BaseEntity<Integer> implements IEntityExecutionVariable {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_VARIAVEL_EXECUCAO_TRANSICAO";

    @Id
    @GeneratedValue(generator = PK_GENERATOR_NAME, strategy = GenerationType.AUTO)
    @Column(name = "CO_VARIAVEL_EXECUCAO_TRANSICAO")
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_VAR_EXEC_TRANS_INST_PROCES"))
    private FLOW_INSTANCE flowInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VARIAVEL", foreignKey = @ForeignKey(name = "FK_VAR_EXEC_TRANS_VAR"))
    private VAR_INSTANCE variable;

    @Column(name = "NO_VARIAVEL", nullable = false, updatable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_TAREFA_ORIGEM", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_VAR_EXEC_TRANS_TAR_ORIGEM"))
    private TASK_INSTANCE originTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_TAREFA_DESTINO", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_VAR_EXEC_TRANS_DEST"))
    private TASK_INSTANCE destinationTask;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_HISTORICO", nullable = false, updatable = false)
    private Date date;

    @Column(name = "VL_NOVO", length = 8000)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_TIPO_VARIAVEL", nullable = false, foreignKey = @ForeignKey(name = "FK_VAR_EXEC_TRANS_TP_VAR"))
    private VAR_TYPE type;

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
