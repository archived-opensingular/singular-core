package br.net.mirante.singular.persistence.service;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import br.net.mirante.singular.persistence.entity.util.SessionWrapper;

public abstract class AbstractHibernateService {

    protected final SessionLocator sessionLocator;

    public AbstractHibernateService(SessionLocator sessionLocator) {
        this.sessionLocator = sessionLocator;
    }

    public SessionLocator getSessionLocator() {
        return sessionLocator;
    }

    protected SessionWrapper getSession() {
        return new SessionWrapper(getSessionLocator().getCurrentSession());
    }

    protected static <T> T newInstanceOf(Class<T> classe) {
        try {
            return classe.newInstance();
        } catch (Exception e) {
            throw new SingularException("Erro instanciando entidade " + classe.getName(), e);
        }
    }
}
