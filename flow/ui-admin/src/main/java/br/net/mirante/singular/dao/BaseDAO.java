package br.net.mirante.singular.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class BaseDAO {

    @Inject
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
