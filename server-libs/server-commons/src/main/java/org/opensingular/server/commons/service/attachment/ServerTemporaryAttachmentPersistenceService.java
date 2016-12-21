package org.opensingular.server.commons.service.attachment;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.service.IFormService;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Transactional
public class ServerTemporaryAttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntitty> extends ServerAbstractAttachmentPersistenceService<T, C> {

    @Inject
    protected transient IFormService formService;

    @Inject
    protected transient IFormAttachmentService formAttachmentService;

    /**
     * Deleta a relacional caso exita
     *
     * @param id       o id do arquivo
     * @param document documento do formulario
     */
    @Override
    public void deleteAttachment(String id, SDocument document) {
        if (StringUtils.isNumeric(id)) {
            deleteAttachment(Long.valueOf(id), formService.findCurrentFormVersion(document));
        }
    }

    private void deleteAttachment(Long id, FormVersionEntity formVersionEntity) {
        if (formVersionEntity != null && id != null) {
            formAttachmentService.deleteFormAttachmentEntity(id, formVersionEntity);
        }
    }


}