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
import org.opensingular.flow.core.SUser;
import org.opensingular.flow.core.entity.IEntityTaskHistoricType;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstanceHistory;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.util.Date;

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
public abstract class AbstractTaskInstanceHistoryEntity<USER extends SUser, TASK_INSTANCE extends IEntityTaskInstance, TASK_HISTORIC_TYPE extends IEntityTaskHistoricType> extends BaseEntity<Integer> implements IEntityTaskInstanceHistory {

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
