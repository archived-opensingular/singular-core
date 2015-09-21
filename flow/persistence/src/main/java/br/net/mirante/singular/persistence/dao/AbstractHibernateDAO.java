package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;
import java.util.Collection;

public abstract class AbstractHibernateDAO<T extends IEntityByCod> {

    protected final SessionLocator sessionLocator;

    public AbstractHibernateDAO(SessionLocator sessionLocator) {
        this.sessionLocator = sessionLocator;
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

    @SuppressWarnings("unchecked")
    public T retrieveByUniqueProperty(Class<T> clazz, String prop, Object o) {
        return (T) getSession().createCriteria(clazz).add(Restrictions.eq(prop, o)).uniqueResult();
    }

}
