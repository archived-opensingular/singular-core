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


import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@GenericGenerator(name = FormAttachmentEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_ANEXO_FORMULARIO", schema = Constants.SCHEMA)
public class FormAttachmentEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_ANEXO_FORMULARIO";

    @Id
    @Column(name = "CO_ANEXO_FORMULARIO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_FORMULARIO")
    private FormVersionEntity formVersionEntity;

    @Column(name = "TX_SHA1", nullable = false)
    private String hashSha1;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public FormVersionEntity getFormVersionEntity() {
        return formVersionEntity;
    }

    public void setFormVersionEntity(FormVersionEntity formVersionEntity) {
        this.formVersionEntity = formVersionEntity;
    }

    public String getHashSha1() {
        return hashSha1;
    }

    public void setHashSha1(String hashSha1) {
        this.hashSha1 = hashSha1;
    }

}