/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.dao.form;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class ExampleDataDAO {

    @Inject
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional
    public void save(ExampleDataDTO o) {
        o.setEditionDate(new Date());
        session().saveOrUpdate(o);
    }

    @Transactional
    public void remove(ExampleDataDTO o) {
        session().delete(o);
    }

    @Transactional @SuppressWarnings("unchecked")
    public List<ExampleDataDTO> list(String typeName, int first, int count, Optional<String> sortProperty, boolean asc) {

        final Criteria crit = session().createCriteria(ExampleDataDTO.class);

        crit.add(Restrictions.eq("type", typeName));
        crit.setFirstResult(first);
        crit.setMaxResults(count);
        crit.addOrder(asc ? Order.asc(sortProperty.orElse("id")) : Order.desc(sortProperty.orElse("id")));

        return crit.list();
    }


    @Transactional @SuppressWarnings("unchecked")
    public Long count(String type) {
        final Criteria crit = session().createCriteria(ExampleDataDTO.class);

        crit.add(Restrictions.eq("type", type));
        crit.setProjection(Projections.count("id"));

        return (Long) crit.uniqueResult();
    }


    @Transactional
    public ExampleDataDTO find(Long id, String  type) {
        Criteria crit = session().createCriteria(ExampleDataDTO.class);
        crit.add(Restrictions.eq("type", type));
        crit.add(Restrictions.eq("id", id));
        return (ExampleDataDTO) crit.uniqueResult();
    }
}
