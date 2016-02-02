package br.net.mirante.singular.pet.server.wicket.dao;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.pet.server.wicket.model.Peticao;

@Repository
public class PeticaoDAO {

    @Inject
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional
    public void save(Peticao o) {
        session().saveOrUpdate(o);
    }

    @Transactional
    public void remove(Peticao o) {
        session().delete(o);
    }

    @Transactional @SuppressWarnings("unchecked")
    public List<Peticao> list(String type) {
        Criteria crit = session().createCriteria(Peticao.class);
        crit.add(Restrictions.eq("type", type));
        return crit.list();
    }

    @Transactional @SuppressWarnings("unchecked")
    public List<Peticao> listAll() {
        Criteria crit = session().createCriteria(Peticao.class);
        return crit.list();
    }

    @Transactional
    public Peticao find(String key, String  type) {
        Criteria crit = session().createCriteria(Peticao.class);
        crit.add(Restrictions.eq("type", type));
        crit.add(Restrictions.eq("key", key));
        return (Peticao) crit.uniqueResult();
    }
}
