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

package org.opensingular.form.exemplos.notificacaosimplificada.domain;

import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "farmacopeia", namespace="http://www.anvisa.gov.br/reg-med/schema/domains")
@XmlType(name = "farmacopeia", namespace="http://www.anvisa.gov.br/reg-med/schema/domains")
@Entity
@Table(name="TB_FARMACOPEIA", schema="DBMEDICAMENTO")
@PrimaryKeyJoinColumn(name="CO_FARMACOPEIA", referencedColumnName="CO_SEQ_VOCABULARIO_CONTROLADO")
@NamedQuery(name="Farmacopeia.findAll", query = "Select farmacopeia From Farmacopeia as farmacopeia where farmacopeia.ativa = 'S'  Order by farmacopeia.descricao  ")
public class Farmacopeia extends VocabularioControlado {

    private static final long serialVersionUID = -4993627813276557221L;

    public Farmacopeia() {}

    public Farmacopeia(Long id, String descricao, SimNao ativa) {
        this.id = id;
        this.descricao = descricao;
        this.ativa = ativa;
    }

}
