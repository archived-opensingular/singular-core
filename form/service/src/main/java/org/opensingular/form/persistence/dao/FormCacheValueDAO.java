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

import org.hibernate.Query;
import org.opensingular.form.persistence.entity.FormCacheValueEntity;
import org.opensingular.lib.support.persistence.BaseDAO;


public class FormCacheValueDAO extends BaseDAO<FormCacheValueEntity, Long> {

    public FormCacheValueDAO() {
        super(FormCacheValueEntity.class);
    }

    public void deleteValuesFromVersion(Long formVersion) {
        Query query = getSession().createQuery("delete FormCacheValueEntity where formVersion.cod = :formVersion");
        query.setParameter("formVersion", formVersion);

        int result = query.executeUpdate();
        getLogger().info("{} itens excluidos na atualização dos dados indexados", result);
    }

    public void deleteAllIndexedData() {
        Query query = getSession().createQuery("delete FormCacheValueEntity");

        int result = query.executeUpdate();
        getLogger().info("{} itens excluidos na atualização dos dados indexados", result);
    }
}