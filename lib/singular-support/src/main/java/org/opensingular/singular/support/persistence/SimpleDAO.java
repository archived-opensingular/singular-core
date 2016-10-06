package org.opensingular.singular.support.persistence;

import org.opensingular.singular.commons.util.Loggable;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Transactional(Transactional.TxType.MANDATORY)
public class SimpleDAO implements Loggable, Serializable {

    @Inject
    protected SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    protected Query setParametersQuery(Query query, Map<String, Object> params) {
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

}