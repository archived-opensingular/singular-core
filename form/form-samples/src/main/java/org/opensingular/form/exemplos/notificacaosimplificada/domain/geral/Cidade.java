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

package org.opensingular.form.exemplos.notificacaosimplificada.domain.geral;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opensingular.lib.support.persistence.entity.BaseEntity;

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


