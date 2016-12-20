package org.opensingular.server.commons.service.attachment;

import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.form.persistence.entity.AttachmentEntity;

import javax.transaction.Transactional;

@Transactional
public class ServerTemporaryAttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntitty> extends ServerAbstractAttachmentPersistenceService<T, C> {

    /**
     * Deleta a relacional caso exita
     * @param id o id do arquivo
     * @param document documento do formulario
     */
    @Override
    public void deleteAttachment(String id, SDocument document) {
        if (findFormAttachmentEntity(id, document) != null) {
            deleteFormAttachmentEntity(id, document);
        }
    }

}