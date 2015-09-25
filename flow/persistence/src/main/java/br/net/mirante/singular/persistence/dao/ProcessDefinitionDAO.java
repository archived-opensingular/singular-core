package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import org.hibernate.criterion.Restrictions;

public class ProcessDefinitionDAO extends AbstractHibernateDAO<ProcessDefinition> {


    public ProcessDefinitionDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }


    public ProcessDefinition retrievePorSigla(String abbreviation) {
        return (ProcessDefinition) getSession()
                .createQuery(" from " + ProcessDefinition.class.getName() + " p where p.abbreviation = :abbreviation ")
                .setParameter("abbreviation", abbreviation)
                .uniqueResult();
    }
}
