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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class FormAnnotationPK implements Serializable {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_FORMULARIO")
    private FormVersionEntity formVersionEntity;

    @Column(name = "CO_CHAVE_ANOTACAO")
    private String classifier;


    public FormVersionEntity getFormVersionEntity() {
        return formVersionEntity;
    }

    public void setFormVersionEntity(FormVersionEntity formVersionEntity) {
        this.formVersionEntity = formVersionEntity;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FormAnnotationPK that = (FormAnnotationPK) o;

        return new EqualsBuilder()
                .append(formVersionEntity, that.formVersionEntity)
                .append(classifier, that.classifier)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(formVersionEntity)
                .append(classifier)
                .toHashCode();
    }
}
