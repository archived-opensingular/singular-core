package org.opensingular.form.persistence.dao;

import org.opensingular.form.persistence.entity.FormAttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntityId;
import org.opensingular.lib.support.persistence.BaseDAO;

import java.util.List;

public class FormAttachmentDAO extends BaseDAO<FormAttachmentEntity, FormAttachmentEntityId> {

    public FormAttachmentDAO() {
        super(FormAttachmentEntity.class);
    }

    public List<FormAttachmentEntity> findFormAttachmentByFormVersionCod(Long formVersionCod) {
        return getSession()
                .createQuery("from FormAttachmentEntity where formVersionEntity.cod = :formVersionCod")
                .setParameter("formVersionCod", formVersionCod)
                .list();
    }

    @Override
    public void saveOrUpdate(FormAttachmentEntity newEntity) {
        super.saveOrUpdate(newEntity);
        getSession().flush();//faz com que o proximo get em formversionetity recupere a relacional
    }

    @Override
    public void delete(FormAttachmentEntity obj) {
        super.delete(obj);
    }
}
