/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.opensingular.flow.core.MUser;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;

/**
 * The persistent class for the TB_ATOR database table.
 */
@Entity
@Table(name = "VW_ATOR", schema = Constants.SCHEMA)
public class Actor extends BaseEntity<Integer> implements MUser {

    /* nao deve ter generator, deve ser uma view*/
    @Id
    @Column(name = "CO_ATOR")
    private Integer cod;

    @Column(name = "CO_USUARIO", nullable = false)
    private String codUsuario;

    @Column(name = "NO_ATOR", nullable = false)
    private String nome;

    @Column(name = "DS_EMAIL", nullable = false)
    private String email;

    public Actor() {
    }

    public Actor(Integer cod, String codUsuario, String nome, String email) {
        this.cod = cod;
        this.codUsuario = codUsuario;
        this.nome = nome;
        this.email = email;
    }

    @Override
    public String getSimpleName() {
        return getNome();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

}