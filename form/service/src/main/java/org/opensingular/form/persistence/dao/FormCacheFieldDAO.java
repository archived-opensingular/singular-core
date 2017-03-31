package org.opensingular.form.persistence.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.opensingular.form.persistence.entity.FormCacheFieldEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

import java.util.List;

public class FormCacheFieldDAO extends BaseDAO<FormCacheFieldEntity, Long> {

    public FormCacheFieldDAO() {
        super(FormCacheFieldEntity.class);
    }

    public FormCacheFieldEntity findFieldByPath(String path) {
        Criteria criteria = getSession().createCriteria(FormCacheFieldEntity.class);
        criteria.add(Restrictions.eq("path", path));
        return (FormCacheFieldEntity) criteria.uniqueResult();
    }

    public List<String> findPathsByName(List<String> paths) {
        Criteria criteria = getSession().createCriteria(tipo);
        criteria.setProjection(Projections.projectionList().add(Projections.property("path")));
        criteria.add(Restrictions.in("path", paths));
        return criteria.list();
    }

    public FormCacheFieldEntity saveOrFind(FormCacheFieldEntity field) {
        FormCacheFieldEntity fieldFromDB = findFieldByPath(field.getPath());
        if (fieldFromDB != null) {
            return fieldFromDB;
        } else {
            save(field);
            return field;
        }
    }

}
