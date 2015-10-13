package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class RoleDAO extends AbstractHibernateDAO<Role> {


    public RoleDAO(SessionLocator sessionLocator) {
        super(Role.class, sessionLocator);
    }
}
