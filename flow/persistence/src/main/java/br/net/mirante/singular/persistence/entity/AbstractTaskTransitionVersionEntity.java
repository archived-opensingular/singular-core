/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.entity;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.flow.core.entity.IEntityTaskTransitionVersion;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;
import br.net.mirante.singular.flow.core.entity.TransitionType;

/**
 * The base persistent class for the TB_VERSAO_TRANSICAO database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractTaskTransitionVersionEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractTaskTransitionVersionEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <TASK_VERSION>
 */
@MappedSuperclass
@Table(name = "TB_VERSAO_TRANSICAO")
public abstract class AbstractTaskTransitionVersionEntity<TASK_VERSION extends IEntityTaskVersion> extends BaseEntity<Integer> implements IEntityTaskTransitionVersion {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_VERSAO_TRANSICAO";

    @Id
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    @Column(name = "CO_VERSAO_TRANSICAO")
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_TAREFA_ORIGEM", nullable = false, updatable = false)
    private TASK_VERSION originTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_TAREFA_DESTINO", nullable = false, updatable = false)
    private TASK_VERSION destinationTask;

    @Column(name = "NO_TRANSICAO", length = 300, nullable = false)
    private String name;

    @Column(name = "SG_TRANSICAO", length = 100, nullable = false)
    private String abbreviation;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_TRANSICAO", nullable = false)
    private TransitionType type;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public TASK_VERSION getOriginTask() {
        return originTask;
    }

    public void setOriginTask(TASK_VERSION originTask) {
        this.originTask = originTask;
    }

    @Override
    public TASK_VERSION getDestinationTask() {
        return destinationTask;
    }

    public void setDestinationTask(TASK_VERSION destinationTask) {
        this.destinationTask = destinationTask;
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
    public String getAbbreviation() {
        return abbreviation;
    }

    @Override
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public TransitionType getType() {
        return type;
    }

    @Override
    public void setType(TransitionType type) {
        this.type = type;
    }

}
