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

package org.opensingular.server.commons.persistence.dao.form;

import org.opensingular.server.commons.persistence.entity.form.FormPetitionEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class FormPetitionDAO extends BaseDAO<FormPetitionEntity, Long> {

    public FormPetitionDAO() {
        super(FormPetitionEntity.class);
    }

    public FormPetitionEntity findFormPetitionEntityByTypeName(Long petitionPK, String typeName) {
        return (FormPetitionEntity) getSession()
                .createCriteria(FormPetitionEntity.class)
                .createAlias("form", "formEntity")
                .createAlias("formEntity.formType", "formType")
                .add(Restrictions.eq("petition.cod", petitionPK))
                .add(Restrictions.eq("formType.abbreviation", typeName))
                .setMaxResults(1)
                .uniqueResult();
    }

    public FormPetitionEntity findFormPetitionEntityByTypeNameAndTask(Long petitionPK, String typeName, Integer taskDefinitionEntityPK) {
        return (FormPetitionEntity) getSession()
                .createCriteria(FormPetitionEntity.class)
                .createAlias("form", "formEntity")
                .createAlias("formEntity.formType", "formType")
                .add(Restrictions.eq("petition.cod", petitionPK))
                .add(Restrictions.eq("formType.abbreviation", typeName))
                .add(Restrictions.eq("taskDefinitionEntity.cod", taskDefinitionEntityPK))
                .setMaxResults(1)
                .uniqueResult();
    }

    public FormPetitionEntity findLastFormPetitionEntityByTypeName(Long petitionPK, String typeName) {
        return (FormPetitionEntity) getSession()
                .createCriteria(FormPetitionEntity.class)
                .createAlias("form", "formEntity")
                .createAlias("formEntity.formType", "formType")
                .createAlias("formEntity.currentFormVersionEntity", "currentFormVersion")
                .add(Restrictions.eq("petition.cod", petitionPK))
                .add(Restrictions.eq("formType.abbreviation", typeName))
                .addOrder(Order.desc("currentFormVersion.inclusionDate"))
                .setMaxResults(1)
                .uniqueResult();
    }
}
