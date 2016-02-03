package br.net.mirante.singular.pet.server.wicket.dao;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
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

    @Transactional
    public Long countQuickSearch(String filter) {
        Query query = createQuery(filter, null, false, true);
        return (Long) query.uniqueResult();
    }

    @Transactional
    public List<Peticao> quickSearch(String filter, int first, int count, String sortProperty, boolean ascending) {
        Query query = createQuery(filter, sortProperty, ascending, false);
        query.setFirstResult(first);
        query.setMaxResults(count);
        return query.list();

    }

    private Query createQuery(String filter, String sortProperty, boolean ascending, boolean count) {

        String hql = "";

        if (count) {
            hql = "SELECT count(p) ";
        }

        hql += "FROM " + Peticao.class.getName() + " p ";

        if (filter != null && !filter.isEmpty()) {
            hql += " WHERE p.description like :filter " +
                    " OR p.process like :filter " +
                    " OR p.creationDate like :filter " +
                    " OR p.id like :filter ";
        }

        if (sortProperty != null) {
            hql += mountSort("p", sortProperty, ascending);
        }

        Query query = session().createQuery(hql);
        if (filter != null && !filter.isEmpty()) {
            query.setParameter("filter", "%" + filter + "%");
        }

        return query;
    }

    private String mountSort(String prefix, String sortProperty, boolean ascending) {
        return " ORDER BY " + prefix + "." + sortProperty +
                (ascending ? " asc " : " desc ");
    }
}
