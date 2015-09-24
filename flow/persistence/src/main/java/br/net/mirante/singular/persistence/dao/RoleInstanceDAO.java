package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.RoleInstance;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class RoleInstanceDAO extends AbstractHibernateDAO<RoleInstance> {


    public RoleInstanceDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

}
