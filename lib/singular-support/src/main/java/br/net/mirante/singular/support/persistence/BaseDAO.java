package br.net.mirante.singular.support.persistence;


import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.persistence.entity.BaseEntity;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional(Transactional.TxType.MANDATORY)
public class BaseDAO<T extends BaseEntity, ID extends Serializable> implements Loggable {

    @Inject
    protected SessionFactory sessionFactory;

    protected Class<T> tipo;


    public BaseDAO() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superclass;
            if (parameterizedType.getActualTypeArguments().length > 0) {
                tipo = (Class<T>) parameterizedType.getActualTypeArguments()[0];
            }
        }
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public ID save(T novoObj) {
        return (ID) getSession().save(novoObj);
    }

    public void saveOrUpdate(T novoObj) {
        getSession().saveOrUpdate(novoObj);
    }

    public T get(ID id) {
        if (id == null) {
            return null;
        } else {
            return (T) getSession().get(tipo, id);
        }
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

    public List<T> listAll() {
        return getSession().createCriteria(tipo).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public T merge(T novoObj) {
        return (T) getSession().merge(novoObj);
    }

    public void delete(T obj) {
        getSession().delete(obj);
    }

    public void evict(Object o) {
        getSession().evict(o);
    }

}
