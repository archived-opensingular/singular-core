/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.dao.form;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class PrototypeDAO {

    @Inject
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional
    public void save(Prototype prototype) {
        session().saveOrUpdate(prototype);
    }

    @Transactional
    public void remove(Prototype prototype) {
        session().delete(prototype);
    }

    @SuppressWarnings("unchecked")
    public List<Prototype> listAll() {
        return session().createCriteria(Prototype.class).list();
    }

    public Prototype findById(Long id) {
        return (Prototype) session().get(Prototype.class, id);
    }
}
