/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.persistence.dao;

import org.opensingular.form.persistence.entity.FormAttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntityId;
import org.opensingular.lib.support.persistence.BaseDAO;

import java.util.List;

public class FormAttachmentDAO extends BaseDAO<FormAttachmentEntity, FormAttachmentEntityId> {

    public FormAttachmentDAO() {
        super(FormAttachmentEntity.class);
    }

    public List<FormAttachmentEntity> findFormAttachmentByFormVersionCod(Long formVersionCod) {
        return getSession()
                .createQuery("from FormAttachmentEntity where formVersionEntity.cod = :formVersionCod")
                .setParameter("formVersionCod", formVersionCod)
                .list();
    }

    @Override
    public void saveOrUpdate(FormAttachmentEntity novoObj) {
        super.saveOrUpdate(novoObj);
        getSession().flush();//faz com que o proximo get em formversionetity recupere a relacional
    }

    @Override
    public void delete(FormAttachmentEntity obj) {
        super.delete(obj);
    }
}
