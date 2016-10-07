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

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

/**
 * The persistent class for the TB_FORMULARIO database table.
 */
@Entity
@GenericGenerator(name = FormEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_FORMULARIO", schema = Constants.SCHEMA)
public class FormEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_FORMULARIO";

    @Id
    @Column(name = "CO_FORMULARIO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_TIPO_FORMULARIO")
    private FormTypeEntity formType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_COLECAO")
    private CollectionEntity collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_ATUAL")
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
