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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import org.opensingular.flow.core.entity.IEntityTaskHistoricType;

/**
 * The base persistent class for the TB_TIPO_HISTORICO_TAREFA database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractTaskHistoricTypeEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractTaskHistoricTypeEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 */
@MappedSuperclass
@Table(name = "TB_TIPO_HISTORICO_TAREFA")
public abstract class AbstractTaskHistoricTypeEntity extends BaseEntity<Integer> implements IEntityTaskHistoricType {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_TIPO_HISTORICO_TAREFA";

    @Id
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    @Column(name = "CO_TIPO_HISTORICO_TAREFA")
    private Integer cod;

    @Column(name = "DS_TIPO_HISTORICO_TAREFA", length = 50)
    private String description;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
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
