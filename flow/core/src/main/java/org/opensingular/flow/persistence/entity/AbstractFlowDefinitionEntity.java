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
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityFlowDefinition;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.core.entity.IEntityModule;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

/**
 * The base persistent class for the flow definition database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name {@link AbstractFlowDefinitionEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractFlowDefinitionEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 */
@MappedSuperclass
@Table(name = "TB_DEFINICAO_PROCESSO")
public abstract class AbstractFlowDefinitionEntity<MODULE extends IEntityModule, CATEGORY extends IEntityCategory, TASK_DEF extends IEntityTaskDefinition, ROLE_DEF extends IEntityRoleDefinition, FLOW_VERSION extends IEntityFlowVersion> extends BaseEntity<Integer> implements
        IEntityFlowDefinition {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_DEFINICAO_PROCESSO";

    @Id
    @Column(name = "CO_DEFINICAO_PROCESSO")
    @GeneratedValue(generator = PK_GENERATOR_NAME, strategy = GenerationType.AUTO)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_CATEGORIA", foreignKey = @ForeignKey(name = "FK_DEFIN_PROCES_CATEGORIA"))
    private CATEGORY category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_MODULO", nullable = false, foreignKey = @ForeignKey(name = "FK_DEFIN_PROCES_MODULO"))
    private MODULE module;

    @Column(name = "SG_PROCESSO", length = 200, nullable = false)
    private String key;

    @Column(name = "NO_PROCESSO", length = 200)
    private String name;

    @Column(name = "NO_CLASSE_JAVA", length = 250, nullable = false)
    private String definitionClassName;

    @OneToMany(mappedBy = "flowDefinition", fetch = FetchType.LAZY)
    private List<TASK_DEF> taskDefinitions;

    @OrderBy("NO_PAPEL")
    @OneToMany(mappedBy = "flowDefinition", fetch = FetchType.LAZY)
    private List<ROLE_DEF> roles = new ArrayList<>();

    @OneToMany(mappedBy = "flowDefinition", fetch = FetchType.LAZY)
    private List<FLOW_VERSION> versions = new ArrayList<>();

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public CATEGORY getCategory() {
        return category;
    }

    @Override
    public void setCategory(IEntityCategory category) {
        this.category = (CATEGORY) category;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
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
    public String getDefinitionClassName() {
        return definitionClassName;
    }

    @Override
    public void setDefinitionClassName(String definitionClassName) {
        this.definitionClassName = definitionClassName;
    }

    @Override
    public List<TASK_DEF> getTaskDefinitions() {
        return taskDefinitions;
    }

    public void setTaskDefinitions(List<TASK_DEF> taskDefinitions) {
        this.taskDefinitions = taskDefinitions;
    }

    @Override
    public List<ROLE_DEF> getRoles() {
        return roles;
    }

    public void setRoles(List<ROLE_DEF> roles) {
        this.roles = roles;
    }

    @Override
    public List<FLOW_VERSION> getVersions() {
        return versions;
    }

    public void setVersions(List<FLOW_VERSION> versions) {
        this.versions = versions;
    }

    @Override
    public MODULE getModule() {
        return module;
    }

    @Override
    public void setModule(IEntityModule module) {
        this.module = (MODULE) module;
    }

}
