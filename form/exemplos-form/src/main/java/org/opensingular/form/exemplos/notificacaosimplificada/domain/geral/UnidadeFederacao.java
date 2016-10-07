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
