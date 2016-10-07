/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.util;

import org.opensingular.flow.persistence.entity.util.SessionLocator;
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
