package org.opensingular.server.commons.service.attachment;

import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.SingularFormPersistenceException;
import org.opensingular.form.persistence.dto.AttachmentRef;
import org.opensingular.form.persistence.entity.AttachmentContentEntity;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.service.IFormService;
import org.opensingular.form.type.core.attachment.AttachmentCopyContext;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.helper.IAttachmentPersistenceHelper;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Transactional
public class ServerAttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntity> extends ServerAbstractAttachmentPersistenceService<T, C> {

    @Inject
    protected transient IFormService formService;

    @Inject
    protected transient IFormAttachmentService formAttachmentService;

    @Inject
    protected transient IAttachmentPersistenceHelper attachmentPersistenceHelper;

    /**
     * Faz o vinculo entre anexo persistido e formversionentity
     *
     * @param ref  referencia a um anexo ja persistido no banco de dados
     * @param sdoc documento atual do formulario
     * @return os dados de contexto para ações pos copia
     */
    @Override
    public AttachmentCopyContext<AttachmentRef> copy(IAttachmentRef ref, SDocument sdoc) {
        if (!(ref instanceof AttachmentRef)) {
            return super.copy(ref, sdoc);
        }
        if (sdoc != null && sdoc.getRoot() != null) {
            formAttachmentService.saveNewFormAttachmentEntity(getAttachmentEntity(ref), formService.findCurrentFormVersion(sdoc));
        }
        return new AttachmentCopyContext<>((AttachmentRef) ref).setDeleteOldFiles(false).setUpdateFileId(false);
    }


    /**
     * Aciona o metodo de deletar a relacional {@link FormAttachmentService#deleteFormAttachmentEntity }
     *
     * @param id       id do anexo
     * @param document o documento atual
     */
    @Override
    public void deleteAttachment(String id, SDocument document) {
        formAttachmentService.deleteFormAttachmentEntity(getAttachmentEntity(id), formService.findCurrentFormVersion(document));
    }

    @Override
    public IAttachmentPersistenceHelper getAttachmentPersistenceHelper() {
        return attachmentPersistenceHelper;
    }

}