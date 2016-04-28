/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.dao;

import br.net.mirante.singular.support.persistence.BaseDAO;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GenericDAO extends BaseDAO {

    public GenericDAO() {
        super(null);
    }

    public <T extends Object> List<T> findByProperty(Class<T> classe, String propertyName, String value) {
        return findByProperty(classe, propertyName, value, null, null);
    }

    public <T extends Object> List<T> findByProperty(Class<T> classe, String propertyName, String value, Integer maxResults) {
        return findByProperty(classe, propertyName, value, null, maxResults);
    }

    public <T> T findByUniqueProperty(Class<T> clazz, String propertyName, Object value){
        return (T) getSession().createCriteria(clazz).add(Restrictions.eq(propertyName, value)).setMaxResults(1).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> List<T> findByProperty(Class<T> classe, String propertyName, String value, MatchMode matchMode, Integer maxResults) {
        Criteria criteria = getSession().createCriteria(classe);

        if (matchMode == null) {
            matchMode = MatchMode.EXACT;
        }

        if (value != null && !value.isEmpty()) {
            criteria.add(Restrictions.ilike(propertyName, value, matchMode));
        }

        if (maxResults != null) {
            criteria.setMaxResults(maxResults);
        }

        return criteria.list();
    }

}
