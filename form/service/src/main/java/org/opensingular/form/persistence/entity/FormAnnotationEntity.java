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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@GenericGenerator(name = FormAnnotationEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_ANOTACAO_FORMULARIO", schema = Constants.SCHEMA)
public class FormAnnotationEntity extends BaseEntity<FormAnnotationPK> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_ANOTACAO_FORM";

    @EmbeddedId
    private FormAnnotationPK cod;

    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_ANOTACAO_ATUAL")
    private FormAnnotationVersionEntity annotationCurrentVersion;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "formAnnotationEntity")
    private List<FormAnnotationVersionEntity> annotationVersions;

    public static String getPkGeneratorName() {

        return PK_GENERATOR_NAME;
    }

    @Override
    public FormAnnotationPK getCod() {
        return cod;
    }

    public void setCod(FormAnnotationPK cod) {
        this.cod = cod;
    }

    public FormAnnotationVersionEntity getAnnotationCurrentVersion() {
        return annotationCurrentVersion;
    }

    public void setAnnotationCurrentVersion(FormAnnotationVersionEntity annotationCurrentVersion) {
        this.annotationCurrentVersion = annotationCurrentVersion;
    }

    public List<FormAnnotationVersionEntity> getAnnotationVersions() {
        return annotationVersions;
    }

    public void setAnnotationVersions(List<FormAnnotationVersionEntity> annotationVersions) {
        this.annotationVersions = annotationVersions;
    }

    public String getClassifier(){
        return getCod().getClassifier();
    }


}
