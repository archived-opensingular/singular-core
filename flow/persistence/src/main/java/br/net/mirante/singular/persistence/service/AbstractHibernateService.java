package br.net.mirante.singular.persistence.service;

import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import org.hibernate.Session;

public abstract class AbstractHibernateService {

    protected final SessionLocator sessionLocator;

    public AbstractHibernateService(SessionLocator sessionLocator) {
        this.sessionLocator = sessionLocator;
    }

    public SessionLocator getSessionLocator() {
        return sessionLocator;
    }

}
