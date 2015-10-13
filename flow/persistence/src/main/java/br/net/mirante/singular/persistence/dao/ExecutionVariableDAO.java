package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.ExecutionVariable;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class ExecutionVariableDAO extends AbstractHibernateDAO<ExecutionVariable> {


    public ExecutionVariableDAO(SessionLocator sessionLocator) {
        super(ExecutionVariable.class, sessionLocator);
    }
}
