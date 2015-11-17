package br.net.mirante.singular.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseDAO {

    /**
     * DBSCHEMA configurado em <code>admin-config.properties</code>
     */
    @Value("#{singularAdmin['database.schema']}")
    protected String DBSCHEMA;

    @Inject
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
