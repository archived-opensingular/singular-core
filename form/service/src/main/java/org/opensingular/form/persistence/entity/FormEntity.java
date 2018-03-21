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
package org.opensingular.form.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;

/**
 * The persistent class for the TB_FORMULARIO database table.
 */
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@SequenceGenerator(name = FormEntity.PK_GENERATOR_NAME, sequenceName = "SQ_CO_FORMULARIO", schema = Constants.SCHEMA)
@Table(name = "TB_FORMULARIO", schema = Constants.SCHEMA)
public class FormEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_FORMULARIO";

    @Id
    @Column(name = "CO_FORMULARIO")
    @GeneratedValue(generator = PK_GENERATOR_NAME, strategy = GenerationType.AUTO)
    private Long cod;

    @ManyToOne
    @JoinColumn(name = "CO_TIPO_FORMULARIO", foreignKey = @ForeignKey(name = "FK_FORMULARIO_TIPO_FORMULARIO"), nullable = false)
    private FormTypeEntity formType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_COLECAO", foreignKey = @ForeignKey(name = "FK_FORMULARIO_COLECAO"))
    private CollectionEntity collection;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "CO_VERSAO_ATUAL", foreignKey = @ForeignKey(name = "FK_FORMULARIO_VERSAO_ATUAL"))
    private FormVersionEntity currentFormVersionEntity;

    //NÃO MAPEAR A LISTA DE VERSÕES NESSA ENTIDADE.
    // FAZER QUERY ESPECÍFICA NA ENTIDADE DE VERSOES DE FORMULARIO

    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public FormTypeEntity getFormType() {
        return formType;
    }

    public void setFormType(FormTypeEntity formType) {
        this.formType = formType;
    }

    public CollectionEntity getCollection() {
        return collection;
    }

    public void setCollection(CollectionEntity collection) {
        this.collection = collection;
    }

    public FormVersionEntity getCurrentFormVersionEntity() {
        return currentFormVersionEntity;
    }

    public void setCurrentFormVersionEntity(FormVersionEntity currentFormVersionEntity) {
        this.currentFormVersionEntity = currentFormVersionEntity;
    }
}
