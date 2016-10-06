/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;

/**
 * The base persistent class for the TB_INSTANCIA_PAPEL database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractRoleInstanceEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractRoleInstance.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <USER>
 * @param <PROCESS_INSTANCE>
 * @param <ROLE_DEF>
 */
@MappedSuperclass
@Table(name = "TB_INSTANCIA_PAPEL")
public abstract class AbstractRoleInstanceEntity<USER extends MUser, PROCESS_INSTANCE extends IEntityProcessInstance, ROLE_DEF extends IEntityRoleDefinition> extends BaseEntity<Integer> implements IEntityRoleInstance {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_INSTANCIA_PAPEL";

    @Id
    @Column(name = "CO_INSTANCIA_PAPEL")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO", nullable = false, updatable = false)
    private PROCESS_INSTANCE processInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_PAPEL", nullable = false, updatable = false)
    private ROLE_DEF role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ATOR", nullable = false)
    private USER user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CRIACAO", nullable = false)
    private Date createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ATOR_ALOCADOR")
    private USER allocatorUser;

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
    public ROLE_DEF getRole() {
        return role;
    }

    public void setRole(ROLE_DEF role) {
        this.role = role;
    }

    @Override
    public USER getUser() {
        return user;
    }

    public void setUser(USER user) {
        this.user = user;
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public USER getAllocatorUser() {
        return allocatorUser;
    }

    public void setAllocatorUser(USER allocatorUser) {
        this.allocatorUser = allocatorUser;
    }

}
