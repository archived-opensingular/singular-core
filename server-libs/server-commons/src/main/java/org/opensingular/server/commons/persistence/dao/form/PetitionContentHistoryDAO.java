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


import org.opensingular.server.commons.persistence.entity.form.FormVersionHistoryEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import org.opensingular.lib.support.persistence.BaseDAO;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;


public class PetitionContentHistoryDAO extends BaseDAO<PetitionContentHistoryEntity, Long> {

    public PetitionContentHistoryDAO() {
        super(PetitionContentHistoryEntity.class);
    }

    public List<PetitionContentHistoryEntity> listPetitionContentHistoryByPetitionCod(long petitionCod) {
        return getSession().createCriteria(PetitionContentHistoryEntity.class).add(Restrictions.eq("petitionEntity.cod", petitionCod)).list();
    }

    public FormVersionHistoryEntity findLastestByPetitionCodAndType(String typeName, Long cod) {
        return (FormVersionHistoryEntity) getSession().createQuery(" select fvhe from PetitionContentHistoryEntity p " +
                " inner join p.formVersionHistoryEntities  fvhe " +
                " inner join fvhe.formVersion fv  " +
                " inner join fv.formEntity fe  " +
                " inner join fe.formType ft  " +
                " where ft.abbreviation = :typeName and p.petitionEntity.cod = :cod " +
                " order by p.historyDate desc ")
                .setParameter("typeName", typeName)
                .setParameter("cod", cod)
                .setMaxResults(1)
                .uniqueResult();
    }
}