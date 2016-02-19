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
public class ExampleDataDAO {

    @Inject
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional
    public void save(ExampleDataDTO o) {
        session().saveOrUpdate(o);
    }

    @Transactional
    public void remove(ExampleDataDTO o) {
        session().delete(o);
    }

    @Transactional @SuppressWarnings("unchecked")
    public List<ExampleDataDTO> list(String type) {
        Criteria crit = session().createCriteria(ExampleDataDTO.class);
        crit.add(Restrictions.eq("type", type));
        return crit.list();
    }

    @Transactional
    public ExampleDataDTO find(String key, String  type) {
        Criteria crit = session().createCriteria(ExampleDataDTO.class);
        crit.add(Restrictions.eq("type", type));
        crit.add(Restrictions.eq("key", key));
        return (ExampleDataDTO) crit.uniqueResult();
    }
}
