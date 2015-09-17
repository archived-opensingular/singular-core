package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.Process;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class ProcessDAO extends AbstractHibernateDAO<Process> {

    public ProcessDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

}
