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

package org.opensingular.form.persistence.dao;

import org.hibernate.Criteria;
import org.hibernate.query.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.opensingular.form.persistence.entity.FormCacheFieldEntity;
import org.opensingular.form.persistence.entity.FormTypeEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

import java.util.List;

public class FormCacheFieldDAO extends BaseDAO<FormCacheFieldEntity, Long> {

    public FormCacheFieldDAO() {
        super(FormCacheFieldEntity.class);
    }

    public FormCacheFieldEntity findField(String path, FormTypeEntity formType) {
        Criteria criteria = getSession().createCriteria(FormCacheFieldEntity.class);
        criteria.add(Restrictions.eq("path", path));
        criteria.add(Restrictions.eq("formTypeEntity.cod", formType.getCod()));
        return (FormCacheFieldEntity) criteria.uniqueResult();
    }

    public List<String> findPathsByName(List<String> paths) {
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.setProjection(Projections.projectionList().add(Projections.property("path")));
        criteria.add(Restrictions.in("path", paths));
        return criteria.list();
    }

    public FormCacheFieldEntity saveOrFind(FormCacheFieldEntity field) {
        FormCacheFieldEntity fieldFromDB = findField(field.getPath(), field.getFormTypeEntity());

        if (fieldFromDB != null) {
            return fieldFromDB;
        } else {
            save(field);
            return field;
        }
    }

    public void deleteAllIndexedFields() {
        Query query = getSession().createQuery("delete FormCacheFieldEntity");

        int result = query.executeUpdate();
        getLogger().info("{} itens excluídos na atualização dos campos indexados", result);
    }

    @SuppressWarnings("unchecked")
    public List<FormCacheFieldEntity> listFields(FormTypeEntity formType) {
        Criteria criteria = getSession().createCriteria(FormCacheFieldEntity.class);
        criteria.add(Restrictions.eq("formTypeEntity.cod", formType.getCod()));
        return criteria.list();
    }
}
