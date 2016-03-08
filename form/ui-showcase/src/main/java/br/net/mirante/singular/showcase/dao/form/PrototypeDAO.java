package br.net.mirante.singular.showcase.dao.form;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
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

    @Transactional
    @SuppressWarnings("unchecked")
    public List<Prototype> listAll() {
        return session().createCriteria(Prototype.class).list();
    }
}
