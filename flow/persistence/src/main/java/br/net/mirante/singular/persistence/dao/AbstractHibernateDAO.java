package br.net.mirante.singular.persistence.dao;

import java.io.Serializable;
import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public abstract class AbstractHibernateDAO<T extends IEntityByCod> {

    protected final Class<T> entityClass;

    protected final SessionLocator sessionLocator;

    public AbstractHibernateDAO(Class<T> entityClass, SessionLocator sessionLocator) {
        this.sessionLocator = sessionLocator;
        this.entityClass = entityClass;
    }

    public SessionLocator getSessionLocator() {
        return sessionLocator;
    }

    protected Session getSession() {
        return sessionLocator.getCurrentSession();
    }

    public void save(T t) {
        getSession().save(t);
    }

    public void save(Collection<T> collection) {
        collection.forEach(this::save);
    }

    public void refresh(T t) {
        getSession().refresh(t);
    }

    public void update(T t) {
        getSession().update(t);
    }

    public void delete(T t) {
        getSession().delete(t);
    }

    public T merge(T t) {
        return (T) getSession().merge(t);
    }

    public final T retrieveById(Serializable id) {
        return entityClass.cast(getSession().load(entityClass, id));
    }

    public T retrieveByUniqueProperty(String prop, Object o) {
        return entityClass.cast(getSession().createCriteria(entityClass).add(Restrictions.eq(prop, o)).uniqueResult());
    }

}
