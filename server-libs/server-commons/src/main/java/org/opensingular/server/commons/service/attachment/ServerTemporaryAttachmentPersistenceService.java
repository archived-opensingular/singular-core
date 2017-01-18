package org.opensingular.server.commons.service.attachment;

import javax.transaction.Transactional;

import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.entity.AttachmentContentEntity;
import org.opensingular.form.persistence.entity.AttachmentEntity;

@Transactional
public class ServerTemporaryAttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntity> extends ServerAbstractAttachmentPersistenceService<T, C> {


    /**
     * Deleta a relacional caso exita
     *
     * @param id       o id do arquivo
     * @param document documento do formulario
     */
    @Override
    public void deleteAttachment(String id, SDocument document) {
        /**
         * do nothing
         */
    }

}