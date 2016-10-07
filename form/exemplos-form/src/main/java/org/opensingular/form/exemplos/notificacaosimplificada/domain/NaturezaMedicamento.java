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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;


@Entity
@Table(name = "TB_NATUREZA_MEDICAMENTO", schema = "DBMEDICAMENTO")
@PrimaryKeyJoinColumn(name = "CO_NATUREZA_MEDICAMENTO", referencedColumnName = "CO_SEQ_VOCABULARIO_CONTROLADO")
@NamedQueries({
        @NamedQuery(name = "NaturezaMedicamento.findAll", query = "Select naturezaMedicamento From NaturezaMedicamento as naturezaMedicamento where naturezaMedicamento.ativa = 'S'  Order by naturezaMedicamento.descricao  ")})
public class NaturezaMedicamento extends VocabularioControlado {

    private static final long serialVersionUID = -1890354175760895205L;

    public NaturezaMedicamento() {
    }

    public NaturezaMedicamento(Long id, String descricao, SimNao ativa) {
        this.id = id;
        this.descricao = descricao;
        this.ativa = ativa;
    }
}
