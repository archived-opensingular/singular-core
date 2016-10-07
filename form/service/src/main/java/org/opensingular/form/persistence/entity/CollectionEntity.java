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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

@Entity
@GenericGenerator(name = CollectionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_COLECAO", schema = Constants.SCHEMA)
public class CollectionEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_COLECAO";

    @Id
    @Column(name = "CO_COLECAO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @Column(name = "NO_COLECAO")
    private String name;

    @ManyToOne
    @JoinColumn(name = "CO_TIPO_FORMULARIO")
    private FormTypeEntity formType;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormTypeEntity getFormType() {
        return formType;
    }

    public void setFormType(FormTypeEntity formType) {
        this.formType = formType;
    }
}
