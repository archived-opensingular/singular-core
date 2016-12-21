package org.opensingular.server.commons.service.attachment;

import org.opensingular.form.persistence.entity.AbstractFormAttachmentEntity;
import org.opensingular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntityId;
import org.opensingular.form.persistence.entity.FormVersionEntity;

import javax.inject.Inject;

public abstract class AbstractFormAttachmentService<T extends AttachmentEntity, C extends AttachmentContentEntitty, F extends AbstractFormAttachmentEntity<T>> {

    @Inject
    private AttachmentService<T, C> attachmentService;

    /**
     * cria a chave utilizando a ref e o documento
     *
     * @param id          do anexo
     * @param formVersion a versao do formulario
     * @return a pk ou null caso nao consiga cronstruir
     */
    public FormAttachmentEntityId createFormAttachmentEntityId(Long id, FormVersionEntity formVersion) {
        AttachmentEntity attachmentEntity = attachmentService.getAttachmentEntity(id);
        if (formVersion != null || attachmentEntity != null) {
            return createFormAttachmentEntityId(formVersion, attachmentEntity);
        }
        return null;
    }

    /**
     * cria a primaria key de form attachment entity
     *
     * @param formVersion      versao do formulario
     * @param attachmentEntity anexo
     * @return a chave instanciada, null caso algum parametro seja nulo
     */
    public FormAttachmentEntityId createFormAttachmentEntityId(FormVersionEntity formVersion, AttachmentEntity attachmentEntity) {
        if (formVersion != null && attachmentEntity != null) {
            return new FormAttachmentEntityId(formVersion.getCod(), attachmentEntity.getCod());
        }
        return null;
    }

}
