package br.net.mirante.singular.form.persistence.dao;

import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.support.persistence.BaseDAO;
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
