/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.support.persistence.util.Constants;
import br.net.mirante.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;

/**
 * The persistent class for the RL_PERMISSAO_TAREFA database table.
 */
@Entity
@Table(name = "RL_PERMISSAO_TAREFA", schema = Constants.SCHEMA)
public class TaskRight {

    @Id
    @Column(name = "CO_PERMISSAO_TAREFA")
    @GeneratedValue(generator = "singular")
    @GenericGenerator(name = "singular", strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
    private Long cod;

    //bi-directional many-to-one association to TaskDefinition
    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_TAREFA")
    private TaskDefinitionEntity taskDefinition;

    @Column(name = "NO_PERFIL", nullable = false)
    private String nomePerfil;

    public TaskRight() {
    }

    public Long getCod() {
        return this.cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public TaskDefinitionEntity getTaskDefinition() {
        return this.taskDefinition;
    }

    public void setTaskDefinition(TaskDefinitionEntity taskDefinition) {
        this.taskDefinition = taskDefinition;
    }

    public String getNomePerfil() {
        return nomePerfil;
    }

    public void setNomePerfil(String nomePerfil) {
        this.nomePerfil = nomePerfil;
    }
}