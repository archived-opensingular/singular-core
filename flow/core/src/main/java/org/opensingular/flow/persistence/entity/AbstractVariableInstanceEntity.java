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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.entity.IEntityVariableType;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

/**
 * The base persistent class for the TB_VARIAVEL database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractVariableInstanceEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractVariableInstanceEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <FLOW_INSTANCE>
 * @param <VAR_TYPE>
 */
@MappedSuperclass
@Table(name = "TB_VARIAVEL")
public abstract class AbstractVariableInstanceEntity<FLOW_INSTANCE extends IEntityFlowInstance, VAR_TYPE extends IEntityVariableType> extends BaseEntity<Integer> implements IEntityVariableInstance {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_VARIAVEL";

    @Id
    @GeneratedValue(generator = PK_GENERATOR_NAME, strategy = GenerationType.AUTO)
    @Column(name = "CO_VARIAVEL")
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO", nullable = false, foreignKey = @ForeignKey(name = "FK_VARIAVEL_INSTANCIA_PROCESSO"))
    private FLOW_INSTANCE flowInstance;

    @Column(name = "NO_VARIAVEL", nullable = false, length = 100)
    private String name;

    @Lob
    @Column(name = "VL_VARIAVEL", length = 8000)
    private String value;

    @ManyToOne
    @JoinColumn(name = "CO_TIPO_VARIAVEL", foreignKey = @ForeignKey(name = "FK_VARIAVEL_TIPO_VARIAVEL"),  nullable = false)
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

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public VAR_TYPE getType() {
        return type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setType(IEntityVariableType type) {
        this.type = (VAR_TYPE) type;
    }

}
