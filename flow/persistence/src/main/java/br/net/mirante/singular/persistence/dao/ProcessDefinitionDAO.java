package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class ProcessDefinitionDAO extends AbstractHibernateDAO<ProcessDefinition> {


    public ProcessDefinitionDAO(SessionLocator sessionLocator) {
        super(ProcessDefinition.class, sessionLocator);
    }
}
