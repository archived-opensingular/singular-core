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
import org.hibernate.criterion.Restrictions;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

import java.util.List;

public class FormDAO extends BaseDAO<FormEntity, Long> {

    public FormDAO() {
        super(FormEntity.class);
    }

    public void delete(Long key) {
        find(key).ifPresent(this::delete);
    }

    @Override
    public void delete(FormEntity formEntity) {
        if(formEntity.getCurrentFormVersionEntity() != null) {
            getSession().delete(formEntity.getCurrentFormVersionEntity());
        }
        getSession().delete(formEntity);
    }

    @Override
    public void saveOrUpdate(FormEntity newEntity) {
        super.saveOrUpdate(newEntity);
    }

    public List<FormEntity> listByFormAbbreviation(String formAbbreviation) {
        return getfindByFormAbbreviation(formAbbreviation).list();
    }

    public List<FormEntity> listByFormAbbreviation(String formAbbreviation, long first, long max) {
        return getfindByFormAbbreviation(formAbbreviation)
                .setFirstResult((int) first)
                .setMaxResults((int) max)
                .list();
    }

    public List<FormEntity> listUnIndexedForms() {
        Criteria criteria = getSession().createCriteria(FormEntity.class);
        criteria.setMaxResults(50);
        criteria.createAlias("currentFormVersionEntity", "formVersion");
        criteria.add(Restrictions.eq("formVersion.indexed", 'N'));
        return criteria.list();
    }

    private Criteria getfindByFormAbbreviation(String formAbbreviation) {
        return getSession().createCriteria(FormEntity.class)
                .createAlias("formType", "formType")
                .add(Restrictions.eq("formType.abbreviation", formAbbreviation));
    }


}