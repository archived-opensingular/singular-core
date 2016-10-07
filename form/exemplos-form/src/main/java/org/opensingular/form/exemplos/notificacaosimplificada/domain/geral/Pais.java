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
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opensingular.lib.support.persistence.entity.BaseEntity;

@XmlType(name = "pais", namespace = "http://www.anvisa.gov.br/geral/schema/domains")
@XmlRootElement(name = "pais", namespace = "http://www.anvisa.gov.br/geral/schema/domains")
@Entity
@Table(name = "TB_PAIS", schema = "DBGERAL")
public class Pais extends BaseEntity<Integer> {

    public static final Integer CODIGO_BRASIL = 1;
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "CO_SEQ_PAIS", nullable = false, precision = 6, scale = 0)
    private Integer id;

    @Column(name = "NO_PAIS", nullable = false, length = 50)
    private String nome;

    @Column(name = "SG_PAIS", length = 3)
    private String sigla;

    @Transient
    public boolean isBrasil() {
        if (this.id != null && this.id.equals(CODIGO_BRASIL)) {
            return true;
        }
        return false;
    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }


    public void setSigla(String sigla) {
        this.sigla = sigla;
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