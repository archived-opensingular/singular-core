package org.opensingular.server.commons.service.attachment;

import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.SingularFormPersistenceException;
import org.opensingular.form.persistence.dto.AttachmentRef;
import org.opensingular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntityId;
import org.opensingular.form.type.core.attachment.AttachmentCopyContext;
import org.opensingular.form.type.core.attachment.IAttachmentRef;

import javax.transaction.Transactional;

@Transactional
public class ServerAttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntitty> extends ServerAbstractAttachmentPersistenceService<T, C> {

    /**
     * Faz o vinculo entre anexo persistido e formversionentity
     * @param ref referencia a um anexo ja persistido no banco de dados
     * @param sdoc documento atual do formulario
     * @return os dados de contexto para ações pos copia
     */
    @Override
    public AttachmentCopyContext<AttachmentRef> copy(IAttachmentRef ref, SDocument sdoc) {
        if (!(ref instanceof AttachmentRef)) {
            throw new SingularFormPersistenceException("A service ServerAttachmentPersistenceService suporta apenas" +
                    " anexos persistidos em banco de dados e referencias do tipo " + AttachmentRef.class.getName());
        }
        if (sdoc != null && sdoc.getRoot() != null) {
            FormAttachmentEntityId formAttachmentPK = createFormAttachmentEntityId(ref, sdoc);
            if (formAttachmentPK != null && formAttachmentDAO.find(formAttachmentPK) == null) {
                formAttachmentDAO.save(new FormAttachmentEntity(formAttachmentPK));
            }
        }
        return new AttachmentCopyContext<>((AttachmentRef) ref).setDeleteOldFiles(false).setUpdateFileId(false);
    }

    /**
     * Aciona o metodo de deletar a relacional {@link ServerAbstractAttachmentPersistenceService#deleteFormAttachmentEntity }
     * @param id id do anexo
     * @param document o documento atual
     */
    @Override
    public void deleteAttachment(String id, SDocument document) {
        deleteFormAttachmentEntity(id, document);
    }

}