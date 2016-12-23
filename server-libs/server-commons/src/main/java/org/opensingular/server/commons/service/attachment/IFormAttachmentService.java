package org.opensingular.server.commons.service.attachment;

import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntityId;
import org.opensingular.form.persistence.entity.FormVersionEntity;

import java.util.List;

public interface IFormAttachmentService {

    void saveNewFormAttachmentEntity(AttachmentEntity attachmentEntity, FormVersionEntity currentFormVersion);

    void deleteFormAttachmentEntity(AttachmentEntity attachmentEntity, FormVersionEntity formVersionEntity);

    FormAttachmentEntity findFormAttachmentEntity(AttachmentEntity attachmentEntity, FormVersionEntity formVersionEntity);

    FormAttachmentEntityId createFormAttachmentEntityId(AttachmentEntity attachmentEntity, FormVersionEntity formVersion);

    FormAttachmentEntityId createFormAttachmentEntityId(FormVersionEntity formVersion, AttachmentEntity attachmentEntity);

    List<FormAttachmentEntity> findAllByVersion(FormVersionEntity formVersionEntity);

    void deleteFormAttachmentEntity(FormAttachmentEntity formAttachmentEntity);
}
