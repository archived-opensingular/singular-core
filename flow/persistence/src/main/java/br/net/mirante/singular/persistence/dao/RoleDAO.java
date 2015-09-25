package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import org.hibernate.criterion.Restrictions;

public class RoleDAO extends AbstractHibernateDAO<Role> {


    public RoleDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }
}
