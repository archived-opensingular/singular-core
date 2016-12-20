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

@Entity
@Table(name = "TB_ANEXO_FORMULARIO", schema = Constants.SCHEMA)
public class FormAttachmentEntity extends BaseEntity<FormAttachmentEntityId> {

    @EmbeddedId
    private FormAttachmentEntityId cod;

    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_FORMULARIO", insertable = false, updatable = false)
    private FormVersionEntity formVersionEntity;

    @ManyToOne
    @JoinColumn(name = "CO_ARQUIVO", insertable = false, updatable = false)
    private AttachmentEntity attachmentEntity;

    public FormAttachmentEntity() {
    }

    public FormAttachmentEntity(FormAttachmentEntityId cod) {
        this.cod = cod;
    }

    public FormVersionEntity getFormVersionEntity() {
        return formVersionEntity;
    }

    public AttachmentEntity getAttachmentEntity() {
        return attachmentEntity;
    }

    @Override
    public FormAttachmentEntityId getCod() {
        return cod;
    }

    public FormAttachmentEntity setCod(FormAttachmentEntityId cod) {
        this.cod = cod;
        return this;
    }
}