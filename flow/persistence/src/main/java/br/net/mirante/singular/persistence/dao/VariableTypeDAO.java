package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.VariableType;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class VariableTypeDAO extends AbstractHibernateDAO<VariableType> {


    public VariableTypeDAO(SessionLocator sessionLocator) {
        super(VariableType.class, sessionLocator);
    }
}
