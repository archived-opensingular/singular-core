/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.persistence.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.opensingular.singular.support.persistence.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import org.opensingular.flow.core.entity.IEntityVariableType;

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
    @GeneratedValue(generator = PK_GENERATOR_NAME)
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
