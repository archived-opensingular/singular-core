package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.ProcessInstance;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.io.Serializable;

public class ProcessInstanceDAO extends AbstractHibernateDAO<ProcessInstance> {


    public ProcessInstanceDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    public ProcessInstance retrieveById(Serializable id) {
        return (ProcessInstance) getSession().load(ProcessInstance.class, id);
    }
}
