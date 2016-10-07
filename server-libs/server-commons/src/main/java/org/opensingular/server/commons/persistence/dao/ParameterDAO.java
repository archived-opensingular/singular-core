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

package org.opensingular.server.commons.persistence.dao;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import org.opensingular.flow.core.entity.IEntityProcessGroup;
import org.opensingular.server.commons.persistence.entity.parameter.ParameterEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

@Transactional(Transactional.TxType.MANDATORY)
public class ParameterDAO extends BaseDAO<ParameterEntity, Long> {

    public ParameterDAO() {
        super(ParameterEntity.class);
    }

    public ParameterEntity findByNameAndProcessGroup(String name, IEntityProcessGroup processGroup) {
        Criteria c = getSession().createCriteria(tipo);
        c.add(Restrictions.eq("name", name));
        c.add(Restrictions.eq("codProcessGroup", processGroup.getCod()));
        c.addOrder(Order.asc("cod"));
        c.setMaxResults(1);
        return (ParameterEntity) c.uniqueResult();
    }
}
