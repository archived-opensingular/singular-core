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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;


@Entity
@Table(name = "TB_CATEG_REGULATORIA_MEDICAMEN", schema = "DBMEDICAMENTO")
@PrimaryKeyJoinColumn(name = "CO_CATEG_REGULA_MEDICAMEN", referencedColumnName = "CO_SEQ_VOCABULARIO_CONTROLADO")
@NamedQueries({
        @NamedQuery(name = "CatRegulatoriaMedicamento.findAll", query = "Select catRegulatoriaMedicamento From CategoriaRegulatoriaMedicamento as catRegulatoriaMedicamento where catRegulatoriaMedicamento.ativa = 'S'  Order by catRegulatoriaMedicamento.descricao  ")})
public class CategoriaRegulatoriaMedicamento extends VocabularioControlado {

    private static final long serialVersionUID = 6455124096930955761L;

    @ManyToOne
    @JoinColumn(name = "CO_NATUREZA_MEDICAMENTO")


    private NaturezaMedicamento naturezaMedicamento;

    public CategoriaRegulatoriaMedicamento() {
    }

    public CategoriaRegulatoriaMedicamento(Long id, String descricao,
                                           SimNao ativa, TipoTermo tipoTermo, NaturezaMedicamento naturezaMedicamento) {
        super(id, descricao, ativa, tipoTermo);
        this.naturezaMedicamento = naturezaMedicamento;
    }

    public NaturezaMedicamento getNaturezaMedicamento() {
        return this.naturezaMedicamento;
    }

    public void setNaturezaMedicamento(NaturezaMedicamento naturezaMedicamento) {
        this.naturezaMedicamento = naturezaMedicamento;
    }

}
