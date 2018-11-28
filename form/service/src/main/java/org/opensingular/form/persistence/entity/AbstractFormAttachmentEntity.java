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

import javax.annotation.Nullable;
import javax.persistence.EmbeddedId;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

@MappedSuperclass
@Table(name = "TB_ANEXO_FORMULARIO", schema = Constants.SCHEMA)
public class AbstractFormAttachmentEntity<T extends AttachmentEntity> extends BaseEntity<FormAttachmentEntityId> {

    @EmbeddedId
    private FormAttachmentEntityId cod;

    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_FORMULARIO", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ANX_FORM_VERSAO_FORMULARIO"))
    private FormVersionEntity formVersionEntity;

    @ManyToOne
    @JoinColumn(name = "CO_ARQUIVO", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ANX_FORM_CO_ARQUIVO"))
    private T attachmentEntity;


    /**
     * Se esse método for chamado durante a mesma transação que o persistiu o valor retornará nulo
     * mesmo que seja feito load ou get. Se precisar recuperar o id, pegar via getCod().
     *
     * @return
     */
    @Nullable
    public FormVersionEntity getFormVersionEntity() {
        return formVersionEntity;
    }

    /**
     * Se esse método for chamado durante a mesma transação que o persistiu o valor retornará nulo
     * mesmo que seja feito load ou get. Se precisar recuperar o id, pegar via getCod().
     *
     * @return
     */
    @Nullable
    public T getAttachmentEntity() {
        return attachmentEntity;
    }

    @Override
    public FormAttachmentEntityId getCod() {
        return cod;
    }

    public void setCod(FormAttachmentEntityId cod) {
        this.cod = cod;
    }

}