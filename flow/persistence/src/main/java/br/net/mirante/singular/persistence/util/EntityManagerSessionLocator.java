package br.net.mirante.singular.persistence.util;

import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import org.hibernate.Session;

import javax.persistence.EntityManager;

public class EntityManagerSessionLocator implements SessionLocator {

    private EntityManager entityManager;

    @Override
    public Session getCurrentSession() {
        return (Session) entityManager.getDelegate();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
