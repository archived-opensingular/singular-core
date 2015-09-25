package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.ExecutionVariable;
import br.net.mirante.singular.persistence.entity.TaskHistoryType;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.io.Serializable;

public class ExecutionVariableDAO extends AbstractHibernateDAO<ExecutionVariable> {


    public ExecutionVariableDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    public ExecutionVariable retrieveById(Serializable id) {
        return (ExecutionVariable) getSession().load(ExecutionVariable.class, id);
    }

}
