package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import org.hibernate.criterion.Restrictions;

public class ProcessDefinitionDAO extends AbstractHibernateDAO {


    public ProcessDefinitionDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }


    public ProcessDefinition retrievePorSigla(String abbreviation) {
        return (ProcessDefinition) getSession()
                .createQuery(" from " + ProcessDefinition.class.getName() + " p where p.abbreviation = :abbreviation ")
                .setParameter("abbreviation", abbreviation)
                .uniqueResult();
    }

    public void save(ProcessDefinition def) {
        getSession().save(def);
    }

    public void refresh(ProcessDefinition def) {
        getSession().refresh(def);
    }

    public void update(ProcessDefinition def) {
        getSession().update(def);
    }

    public void save(Category categoria) {
        getSession().save(categoria);
    }

    public <T extends IEntityByCod> T retrieveByUniqueProperty(Class<T> t, String prop, Object o) {
        return (T) getSession().createCriteria(t).add(Restrictions.eq(prop, o)).uniqueResult();
    }

    public void delete(Role role) {
        getSession().delete(role);
    }

    public void update(Role role) {
        getSession().update(role);
    }

    public void save(Role role) {
        getSession().save(role);
    }
}
