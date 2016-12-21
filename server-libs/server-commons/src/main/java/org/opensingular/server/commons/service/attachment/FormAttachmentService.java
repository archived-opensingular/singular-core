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

    /**
     * Salva uma nova relacional caso não encontre nenhuma para a chave informada
     *
     * @param formAttachmentPK o id do FormAttachmentEntity
     * @return a FormAttachmentEntity salva ou recupera do banco caso ja exita
     */
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
    public void saveNewFormAttachmentEntity(Long attachmentID, FormVersionEntity currentFormVersion) {
        saveNewFormAttachmentEntity(createFormAttachmentEntityId(attachmentID, currentFormVersion));
    }


    /**
     * Deleta a relacional entre anexo e formversionentity
     *
     * @param id                o id do anexo
     * @param formVersionEntity a versao atual(FormVersioEntity)
     */
    @Override
    public void deleteFormAttachmentEntity(Long id, FormVersionEntity formVersionEntity) {
        FormAttachmentEntity formAttachmentEntity = findFormAttachmentEntity(id, formVersionEntity);
        if (formAttachmentEntity != null) {
            formAttachmentDAO.delete(formAttachmentEntity);
        }
    }

    /**
     * Busca a relacional pelo id do anexo e documento
     *
     * @param id                o id do anexo
     * @param formVersionEntity a versao atual (FormVersioEntity)
     * @return a entidade que relacionada anexo e formversion ou null caso nao encontre
     */
    @Override
    public FormAttachmentEntity findFormAttachmentEntity(Long id, FormVersionEntity formVersionEntity) {
        FormAttachmentEntityId formAttachmentPK = createFormAttachmentEntityId(id, formVersionEntity);
        if (formAttachmentPK != null) {
            return formAttachmentDAO.find(formAttachmentPK);
        }
        return null;
    }

}