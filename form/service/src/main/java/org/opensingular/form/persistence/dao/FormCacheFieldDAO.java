package org.opensingular.form.persistence.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.opensingular.form.persistence.entity.FormCacheFieldEntity;
import org.opensingular.form.persistence.entity.FormTypeEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

import java.util.List;

public class FormCacheFieldDAO extends BaseDAO<FormCacheFieldEntity, Long> {

    public FormCacheFieldDAO() {
        super(FormCacheFieldEntity.class);
    }

    public FormCacheFieldEntity findField(String path, FormTypeEntity formType) {
        Criteria criteria = getSession().createCriteria(FormCacheFieldEntity.class);
        criteria.add(Restrictions.eq("path", path));
        criteria.add(Restrictions.eq("formTypeEntity.cod", formType.getCod()));
        return (FormCacheFieldEntity) criteria.uniqueResult();
    }

    public List<String> findPathsByName(List<String> paths) {
        Criteria criteria = getSession().createCriteria(tipo);
        criteria.setProjection(Projections.projectionList().add(Projections.property("path")));
        criteria.add(Restrictions.in("path", paths));
        return criteria.list();
    }

    public FormCacheFieldEntity saveOrFind(FormCacheFieldEntity field) {
        FormCacheFieldEntity fieldFromDB = findField(field.getPath(), field.getFormTypeEntity());

        if (fieldFromDB != null) {
            return fieldFromDB;
        } else {
            save(field);
            return field;
        }
    }

    public void deleteAllIndexedFields() {
        Query query = getSession().createQuery("delete FormCacheFieldEntity");

        int result = query.executeUpdate();
        getLogger().info("{} itens excluidos na atualização dos campos indexados", result);
    }
}
