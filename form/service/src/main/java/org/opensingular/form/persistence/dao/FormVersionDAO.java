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

import org.hibernate.criterion.Restrictions;
import org.opensingular.form.SIComposite;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

import java.util.List;
import java.util.Optional;

public class FormVersionDAO extends BaseDAO<FormVersionEntity, Long> {

    public FormVersionDAO() {
        super(FormVersionEntity.class);
    }


    public List<FormVersionEntity> findVersions(FormEntity form) {
        return getSession().createCriteria(FormVersionEntity.class)
                .add(Restrictions.eq("formEntity", form))
                .list();
    }

    @Override
    public void delete(FormVersionEntity obj) {
        super.delete(obj);
        getSession().flush();
    }

    public void resetIndexedFlag() {
        List<FormVersionEntity> formVersionEntities = super.listAll();
        formVersionEntities.forEach(fv -> fv.setIndexed('N'));
        getSession().flush();
    }

}
