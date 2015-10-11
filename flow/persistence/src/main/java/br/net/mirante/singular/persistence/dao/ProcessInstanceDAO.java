package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.ProcessInstance;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class ProcessInstanceDAO extends AbstractHibernateDAO<ProcessInstance> {

    public ProcessInstanceDAO(SessionLocator sessionLocator) {
        super(ProcessInstance.class, sessionLocator);
    }
}
