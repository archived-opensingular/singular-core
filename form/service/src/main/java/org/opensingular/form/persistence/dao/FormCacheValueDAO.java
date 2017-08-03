package org.opensingular.form.persistence.dao;

import org.hibernate.Query;
import org.opensingular.form.persistence.entity.FormCacheValueEntity;
import org.opensingular.lib.support.persistence.BaseDAO;


public class FormCacheValueDAO extends BaseDAO<FormCacheValueEntity, Long> {

    public FormCacheValueDAO() {
        super(FormCacheValueEntity.class);
    }

    public void deleteValuesFromVersion(Long formVersion) {
        Query query = getSession().createQuery("delete FormCacheValueEntity where formVersion.cod = :formVersion");
        query.setParameter("formVersion", formVersion);

        int result = query.executeUpdate();
        getLogger().info("{} itens excluidos na atualização dos dados indexados", result);
    }

    public void deleteAllIndexedData() {
        Query query = getSession().createQuery("delete FormCacheValueEntity");

        int result = query.executeUpdate();
        getLogger().info("{} itens excluidos na atualização dos dados indexados", result);
    }
}
