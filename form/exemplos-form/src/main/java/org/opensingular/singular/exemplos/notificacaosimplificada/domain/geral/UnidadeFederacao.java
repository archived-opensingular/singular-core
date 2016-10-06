/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.exemplos.notificacaosimplificada.domain.geral;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opensingular.singular.support.persistence.entity.BaseEntity;

@Entity
@Table(name = "TB_UNIDADE_FEDERACAO", schema = "DBGERAL")
@XmlRootElement(name = "unidadeFederacao", namespace = "http://www.anvisa.gov.br/geral/schema/domains")
@XmlType(name = "unidadeFederacao", namespace = "http://www.anvisa.gov.br/geral/schema/domains")
public class UnidadeFederacao extends BaseEntity<String> {

    public static final String CODIGO_UF_NAO_INFORMADO = "99";
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "CO_UF", nullable = false, length = 2)
    private String id;

    @Column(name = "NO_UF", nullable = false, length = 50)
    private String nome;

    @Transient
    public String getSigla() {
        if (CODIGO_UF_NAO_INFORMADO.equals(this.id)) {
            return "Exterior";
        } else {
            return this.id;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getCod() {
        return id;
    }
}
