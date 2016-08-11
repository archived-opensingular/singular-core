/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.support.persistence;


import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import br.net.mirante.singular.commons.util.Loggable;

@Transactional(Transactional.TxType.MANDATORY)
public class SimpleDAO implements Loggable, Serializable {

    @Inject
    protected transient SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Query setParametersQuery(Query query, Map<String, Object> params) {
        for (Map.Entry<String, Object> parameter : params.entrySet()) {
            if (parameter.getValue() instanceof Collection<?>) {
                query.setParameterList(parameter.getKey(),
                        (Collection<?>) parameter.getValue());
            } else if (parameter.getValue() instanceof Integer) {
                query.setInteger(parameter.getKey(), (Integer) parameter.getValue());
            } else if (parameter.getValue() instanceof Date) {
                query.setDate(parameter.getKey(), (Date) parameter.getValue());
            } else {
                query.setParameter(parameter.getKey(), parameter.getValue());
            }
        }
        return query;
    }

    public void evict(Object o) {
        getSession().evict(o);
    }

}
