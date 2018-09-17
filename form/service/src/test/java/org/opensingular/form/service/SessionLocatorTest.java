package org.opensingular.form.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.opensingular.lib.support.persistence.SessionLocator;

public class SessionLocatorTest implements SessionLocator {

    private SessionFactory sessionFactory;

    @Override
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
