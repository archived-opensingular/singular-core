/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
