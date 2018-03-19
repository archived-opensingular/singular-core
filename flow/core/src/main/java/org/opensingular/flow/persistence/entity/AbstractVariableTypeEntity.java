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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.opensingular.flow.core.entity.IEntityVariableType;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

/**
 * The base persistent class for the TB_TIPO_VARIAVEL database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractVariableTypeEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractVariableTypeEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 */
@MappedSuperclass
@Table(name = "TB_TIPO_VARIAVEL")
public class AbstractVariableTypeEntity extends BaseEntity<Integer> implements IEntityVariableType {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_TIPO_VARIAVEL";

    @Id
    @Column(name = "CO_TIPO_VARIAVEL")
    @GeneratedValue(generator = PK_GENERATOR_NAME, strategy = GenerationType.AUTO)
    private Integer cod;

    @Column(name = "NO_CLASSE_JAVA")
    private String typeClassName;

    @Column(name = "DS_TIPO_VARIAVEL")
    private String description;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public String getTypeClassName() {
        return typeClassName;
    }

    @Override
    public void setTypeClassName(String typeClassName) {
        this.typeClassName = typeClassName;
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
