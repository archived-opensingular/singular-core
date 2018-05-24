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

import java.util.List;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityFlowDefinition;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

/**
 * The base persistent class for the TB_CATEGORIA database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractCategoryEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractCategoryEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <FLOW_DEFINITION>
 */
@MappedSuperclass
@Table(name = "TB_CATEGORIA")
public abstract class AbstractCategoryEntity<FLOW_DEFINITION extends IEntityFlowDefinition> extends BaseEntity<Integer> implements IEntityCategory {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_CATEGORIA";
    
    @Id
    @Column(name = "CO_CATEGORIA")
    @GeneratedValue(generator = PK_GENERATOR_NAME, strategy = GenerationType.AUTO)
    private Integer cod;

    @Column(name = "NO_CATEGORIA", length = 100, nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private List<FLOW_DEFINITION> flowDefinitions;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<FLOW_DEFINITION> getFlowDefinitions() {
        return flowDefinitions;
    }

    public void setFlowDefinitions(List<FLOW_DEFINITION> flowDefinitions) {
        this.flowDefinitions = flowDefinitions;
    }

}
