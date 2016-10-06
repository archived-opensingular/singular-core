/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.exemplos.notificacaosimplificada.domain.geral;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opensingular.singular.support.persistence.entity.BaseEntity;

@Entity
@Table(name = "TB_CIDADE", schema = "DBGERAL")
@XmlRootElement(name = "cidade", namespace = "http://www.anvisa.gov.br/geral/schema/domains")
@XmlType(name = "cidade", namespace = "http://www.anvisa.gov.br/geral/schema/domains")
public class Cidade extends BaseEntity<Integer> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_SEQ_CIDADE", nullable = false, precision = 6, scale = 0)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_UF", nullable = false)
    private UnidadeFederacao uf;

    @Column(name = "NO_CIDADE", nullable = false, length = 50)
    private String nome;

    @JoinColumn(name = "CO_PAIS", referencedColumnName = "CO_SEQ_PAIS")
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Pais pais;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UnidadeFederacao getUf() {
        return uf;
    }

    public void setUf(UnidadeFederacao uf) {
        this.uf = uf;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public Integer getCod() {
        return id;
    }

    @Override
    public String toString() {
        return nome;
    }
}


