/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.util;

import javax.persistence.EntityManager;

import org.hibernate.Session;

import br.net.mirante.singular.form.persistence.entity.util.SessionLocator;

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
