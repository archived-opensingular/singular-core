package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.*;
import br.net.mirante.singular.persistence.entity.Process;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.io.Serializable;

public class VariableDAO extends AbstractHibernateDAO<Variable> {


    public VariableDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    public Variable retrieveById(Serializable id) {
        return (Variable) getSession().load(Variable.class, id);
    }

}
