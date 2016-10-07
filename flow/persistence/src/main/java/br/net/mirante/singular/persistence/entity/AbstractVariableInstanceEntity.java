/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.entity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableType;

/**
 * The base persistent class for the TB_VARIAVEL database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractVariableInstanceEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractVariableInstanceEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <PROCESS_INSTANCE>
 * @param <VAR_TYPE>
 */
@MappedSuperclass
@Table(name = "TB_VARIAVEL")
public abstract class AbstractVariableInstanceEntity<PROCESS_INSTANCE extends IEntityProcessInstance, VAR_TYPE extends IEntityVariableType> extends BaseEntity<Integer> implements IEntityVariableInstance {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_VARIAVEL";

    @Id
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    @Column(name = "CO_VARIAVEL")
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO", nullable = false)
    private PROCESS_INSTANCE processInstance;

    @Column(name = "NO_VARIAVEL", nullable = false)
    private String name;

    @Column(name = "VL_VARIAVEL", length = 1000)
    private String value;

    @ManyToOne
    @JoinColumn(name = "CO_TIPO_VARIAVEL")
    private VAR_TYPE type;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public PROCESS_INSTANCE getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(PROCESS_INSTANCE processInstance) {
        this.processInstance = processInstance;
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
