package org.opensingular.singular.form.persistence.dao;

import org.opensingular.singular.form.persistence.entity.FormTypeEntity;
import org.opensingular.singular.support.persistence.BaseDAO;
import org.hibernate.criterion.Restrictions;

public class FormTypeDAO extends BaseDAO<FormTypeEntity, Integer> {

    public FormTypeDAO() {
        super(FormTypeEntity.class);
    }

    public FormTypeEntity findFormTypeByAbbreviation(String abbreviation) {
        return (FormTypeEntity) getSession()
                .createCriteria(FormTypeEntity.class)
                .add(Restrictions.eq("abbreviation", abbreviation))
                .setMaxResults(1)
                .uniqueResult();
    }

}
