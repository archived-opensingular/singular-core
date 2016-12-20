package org.opensingular.form.persistence.dao;

import org.opensingular.form.persistence.entity.FormAttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntityId;
import org.opensingular.lib.support.persistence.BaseDAO;

public class FormAttachmentDAO extends BaseDAO<FormAttachmentEntity, FormAttachmentEntityId> {

    public FormAttachmentDAO() {
        super(FormAttachmentEntity.class);
    }
}
