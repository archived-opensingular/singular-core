package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import org.hibernate.Session;

public abstract class AbstractHibernateDAO {

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


}
