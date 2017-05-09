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

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import org.opensingular.flow.core.IEntityTaskType;
import org.opensingular.flow.core.entity.IEntityProcessVersion;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskTransitionVersion;
import org.opensingular.flow.core.entity.IEntityTaskVersion;

/**
 * The base persistent class for the TB_VERSAO_TAREFA database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractEntityTaskVersion#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractEntityTaskVersion.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <PROCESS_VERSION>
 * @param <TASK_DEF>
 * @param <TASK_TRANSITION_VERSION>
 */
@MappedSuperclass
@Table(name = "TB_VERSAO_TAREFA")
public abstract class AbstractEntityTaskVersion<PROCESS_VERSION extends IEntityProcessVersion, TASK_DEF extends IEntityTaskDefinition, TASK_TRANSITION_VERSION extends IEntityTaskTransitionVersion, TASK_TYPE extends Enum<?> & IEntityTaskType> extends BaseEntity<Integer> implements IEntityTaskVersion {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_VERSAO_TAREFA";

    @Id
    @Column(name = "CO_VERSAO_TAREFA")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_PROCESSO", nullable = false)
    private PROCESS_VERSION processVersion;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "CO_DEFINICAO_TAREFA", nullable = false)
    private TASK_DEF taskDefinition;

    @Column(name = "NO_TAREFA", length = 300, nullable = false)
    private String name;

    @Column(name = "CO_TIPO_TAREFA", nullable = false)
    private TASK_TYPE type;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "originTask")
    private List<TASK_TRANSITION_VERSION> transitions = new ArrayList<>();

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
    public TASK_DEF getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TASK_DEF taskDefinition) {
        this.taskDefinition = taskDefinition;
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
    public TASK_TYPE getType() {
        return type;
    }

    public void setType(TASK_TYPE type) {
        this.type = type;
    }

    @Override
    public List<TASK_TRANSITION_VERSION> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<TASK_TRANSITION_VERSION> transitions) {
        this.transitions = transitions;
    }
}
