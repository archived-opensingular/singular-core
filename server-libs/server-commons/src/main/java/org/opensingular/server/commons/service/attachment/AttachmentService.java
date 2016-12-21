package org.opensingular.server.commons.service.attachment;

import org.hibernate.Hibernate;
import org.opensingular.form.persistence.dao.AttachmentDao;
import org.opensingular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.form.persistence.entity.AttachmentEntity;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Transactional
public class AttachmentService<T extends AttachmentEntity, C extends AttachmentContentEntitty> {

    @Inject
    protected AttachmentDao<T, C> attachmentDao;

    public T getAttachmentEntity(Long id) {
        T entity = attachmentDao.find(id);
        Hibernate.initialize(entity);
        return entity;
    }

}
