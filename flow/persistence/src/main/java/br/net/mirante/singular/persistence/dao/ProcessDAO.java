package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.Process;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.io.Serializable;

public class ProcessDAO extends AbstractHibernateDAO<Process> {

    public ProcessDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    public Process retrieveById(Serializable id) {
        return (Process) getSession().load(Process.class, id);
    }

}
