package org.opensingular.server.commons.service.attachment;

import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntityId;
import org.opensingular.form.persistence.entity.FormVersionEntity;

public interface IFormAttachmentService {

    void saveNewFormAttachmentEntity(Long attachmentID, FormVersionEntity currentFormVersion);

    void deleteFormAttachmentEntity(Long id, FormVersionEntity formVersionEntity);

    FormAttachmentEntity findFormAttachmentEntity(Long id, FormVersionEntity formVersionEntity);

    FormAttachmentEntityId createFormAttachmentEntityId(Long id, FormVersionEntity formVersion);

    FormAttachmentEntityId createFormAttachmentEntityId(FormVersionEntity formVersion, AttachmentEntity attachmentEntity);

}
