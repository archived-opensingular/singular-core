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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.opensingular.lib.support.persistence.entity.BaseEntity;


@Entity
@Table(name = "TB_TIPO_UNIDADE_MEDICAMENTO", schema = "DBMEDICAMENTO")
public class TipoMedida extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1762089876181396422L;

    private Long   id;
    private String descricao;

    @Id
    @Column(name = "CO_SEQ_TIPO_UNID_MEDICAMENTO")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "DS_TIPO_UNIDADE_MEDICAMENTO")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    @Transient
    public Long getCod() {
        return id;
    }

    public void setCod(Long id) {
        this.id = id;
    }

}
