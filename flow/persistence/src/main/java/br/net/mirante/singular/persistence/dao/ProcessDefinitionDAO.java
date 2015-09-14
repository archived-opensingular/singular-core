package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class ProcessDefinitionDAO extends AbstractHibernateDAO {


    public ProcessDefinitionDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }


    public ProcessDefinition retrievePorSigla(String abbreviation) {
        return (ProcessDefinition) getSession()
                .createQuery(" from " + ProcessDefinition.class.getName() + " p where p.abbreviation = : abbreviation")
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
}
