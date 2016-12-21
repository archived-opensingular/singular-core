package org.opensingular.server.commons.service.attachment;

import org.opensingular.form.persistence.dao.FormAttachmentDAO;
import org.opensingular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntityId;
import org.opensingular.form.persistence.entity.FormVersionEntity;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Transactional
public class FormAttachmentService extends AbstractFormAttachmentService<AttachmentEntity, AttachmentContentEntitty, FormAttachmentEntity> {

    @Inject
    private FormAttachmentDAO formAttachmentDAO;

    private FormAttachmentEntity saveNewFormAttachmentEntity(FormAttachmentEntityId formAttachmentPK) {
        if (formAttachmentPK != null) {
            FormAttachmentEntity fae = formAttachmentDAO.find(formAttachmentPK);
            if (fae == null) {
                formAttachmentDAO.save(fae = new FormAttachmentEntity(formAttachmentPK));
            }
            return fae;
        }
        return null;
    }

    @Override
    public void saveNewFormAttachmentEntity(AttachmentEntity attachmentEntity, FormVersionEntity currentFormVersion) {
        saveNewFormAttachmentEntity(createFormAttachmentEntityId(attachmentEntity, currentFormVersion));
    }

    @Override
    public void deleteFormAttachmentEntity(AttachmentEntity attachmentEntity, FormVersionEntity formVersionEntity) {
        FormAttachmentEntity formAttachmentEntity = findFormAttachmentEntity(attachmentEntity, formVersionEntity);
        if (formAttachmentEntity != null) {
            formAttachmentDAO.delete(formAttachmentEntity);
        }
    }

    @Override
    public FormAttachmentEntity findFormAttachmentEntity(AttachmentEntity attachmentEntity, FormVersionEntity formVersionEntity) {
        FormAttachmentEntityId formAttachmentPK = createFormAttachmentEntityId(attachmentEntity, formVersionEntity);
        if (formAttachmentPK != null) {
            return formAttachmentDAO.find(formAttachmentPK);
        }
        return null;
    }

}