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

import org.hibernate.annotations.GenericGenerator;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleTask;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;

/**
 * The base persistent class for the RL_PAPEL_TAREFA database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractRoleTaskEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractRoleTaskEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <TASK_DEF>
 * @param <ROLE_DEFINITION>
 */
@MappedSuperclass
@Table(name = "RL_PAPEL_TAREFA", schema = Constants.SCHEMA)
public abstract class AbstractRoleTaskEntity<TASK_DEF extends IEntityTaskDefinition, ROLE_DEFINITION extends IEntityRoleDefinition> extends BaseEntity<Integer> implements IEntityRoleTask {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_PAPEL_TAREFA";

    @Id
    @Column(name = "CO_PAPEL_TAREFA")
    @GeneratedValue(generator = PK_GENERATOR_NAME, strategy = GenerationType.AUTO)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_PAPEL", nullable = false)
    private ROLE_DEFINITION roleDefinition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_TAREFA", nullable = false, foreignKey = @ForeignKey(name = "FK_DEFINICAO_TAREFA"))
    private TASK_DEF taskDefinition;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public ROLE_DEFINITION getRoleDefinition() {
        return roleDefinition;
    }

    public void setRoleDefinition(ROLE_DEFINITION roleDefinition) {
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
