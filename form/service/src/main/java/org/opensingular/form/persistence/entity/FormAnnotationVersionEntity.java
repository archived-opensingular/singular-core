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

import javax.persistence.*;
import java.util.Date;

//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@SequenceGenerator(name = FormAnnotationVersionEntity.PK_GENERATOR_NAME, sequenceName = Constants.SCHEMA + ".SQ_CO_VERSAO_ANOTACAO", schema = Constants.SCHEMA)
@Table(name = "TB_VERSAO_ANOTACAO_FORMULARIO", schema = Constants.SCHEMA)
public class FormAnnotationVersionEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_VERSAO_ANOTACAO";

    @Id
    @Column(name = "CO_VERSAO_ANOTACAO")
    @GeneratedValue(generator = PK_GENERATOR_NAME, strategy = GenerationType.AUTO)
    private Long cod;

    @ManyToOne()
    @JoinColumns(value = {
            @JoinColumn(name = "CO_VERSAO_FORMULARIO", referencedColumnName = "CO_VERSAO_FORMULARIO", nullable = false),
            @JoinColumn(name = "CO_CHAVE_ANOTACAO", referencedColumnName = "CO_CHAVE_ANOTACAO", nullable = false)
    }, foreignKey = @ForeignKey(name = "FK_VER_ANOT_FORM_CHV_ANOT"))
    private FormAnnotationEntity formAnnotationEntity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INCLUSAO", nullable = false)
    private Date inclusionDate;

    @Lob
    @Column(name = "XML_ANOTACAO", nullable = false)
    private String xml;

    @Column(name = "CO_AUTOR_INCLUSAO")
    private Integer inclusionActor;

    public FormAnnotationVersionEntity() {
        setInclusionDate(new Date());
    }

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public FormAnnotationEntity getFormAnnotationEntity() {
        return formAnnotationEntity;
    }

    public void setFormAnnotationEntity(FormAnnotationEntity formAnnotationEntity) {
        this.formAnnotationEntity = formAnnotationEntity;
    }

    public Date getInclusionDate() {
        return inclusionDate;
    }

    public void setInclusionDate(Date inclusionDate) {
        this.inclusionDate = inclusionDate;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public Integer getInclusionActor() {
        return inclusionActor;
    }

    public void setInclusionActor(Integer inclusionActor) {
        this.inclusionActor = inclusionActor;
    }
}
