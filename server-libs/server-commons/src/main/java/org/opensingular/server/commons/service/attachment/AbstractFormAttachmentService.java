package org.opensingular.server.commons.service.attachment;

import org.opensingular.form.persistence.entity.AbstractFormAttachmentEntity;
import org.opensingular.form.persistence.entity.AttachmentContentEntity;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntityId;
import org.opensingular.form.persistence.entity.FormVersionEntity;

public abstract class AbstractFormAttachmentService<T extends AttachmentEntity, C extends AttachmentContentEntity, F extends AbstractFormAttachmentEntity<T>> implements IFormAttachmentService {

    /**
     * cria a chave utilizando a ref e o documento
     *
     * @param formVersion a versao do formulario
     * @return a pk ou null caso nao consiga cronstruir
     */
    @Override
    public FormAttachmentEntityId createFormAttachmentEntityId(AttachmentEntity attachmentEntity, FormVersionEntity formVersion) {
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
    @Override
    public FormAttachmentEntityId createFormAttachmentEntityId(FormVersionEntity formVersion, AttachmentEntity attachmentEntity) {
        if (formVersion != null && attachmentEntity != null) {
            return new FormAttachmentEntityId(formVersion.getCod(), attachmentEntity.getCod());
        }
        return null;
    }

}
