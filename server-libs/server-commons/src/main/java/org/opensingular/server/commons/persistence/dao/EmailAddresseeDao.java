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

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import org.opensingular.server.commons.persistence.entity.email.EmailAddresseeEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

@SuppressWarnings("unchecked")
@Transactional(Transactional.TxType.MANDATORY)
public class EmailAddresseeDao<T extends EmailAddresseeEntity> extends BaseDAO<T, Long>{
    
    public EmailAddresseeDao() {
        super((Class<T>) EmailAddresseeEntity.class);
    }

    public EmailAddresseeDao(Class<T> tipo) {
        super(tipo);
    }

    public int countPending() {
        Criteria c = getSession().createCriteria(tipo);
        c.add(Restrictions.isNull("sentDate"));
        c.setProjection(Projections.rowCount());
        return ((Number) c.uniqueResult()).intValue();
    }
    
    public List<T> listPending(int firstResult, int maxResults){
        Criteria c = getSession().createCriteria(tipo);
        c.add(Restrictions.isNull("sentDate"));
        c.addOrder(Order.asc("cod"));
        c.setFirstResult(firstResult);
        c.setMaxResults(maxResults);
        return c.list();
    }
}
