package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class CategoryDAO extends AbstractHibernateDAO<Category> {

    public CategoryDAO(SessionLocator sessionLocator) {
        super(Category.class, sessionLocator);
    }

}
