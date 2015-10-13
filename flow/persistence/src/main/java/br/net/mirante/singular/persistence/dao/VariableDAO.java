package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.Variable;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class VariableDAO extends AbstractHibernateDAO<Variable> {


    public VariableDAO(SessionLocator sessionLocator) {
        super(Variable.class, sessionLocator);
    }
}
