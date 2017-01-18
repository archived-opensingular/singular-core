package org.opensingular.form.persistence.entity;


import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;

import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

@MappedSuperclass
@Table(name = "TB_ANEXO_FORMULARIO", schema = Constants.SCHEMA)
public class AbstractFormAttachmentEntity<T extends AttachmentEntity> extends BaseEntity<FormAttachmentEntityId> {

    @EmbeddedId
    private FormAttachmentEntityId cod;

    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_FORMULARIO", insertable = false, updatable = false)
    private FormVersionEntity formVersionEntity;

    @ManyToOne
    @JoinColumn(name = "CO_ARQUIVO", insertable = false, updatable = false)
    private T attachmentEntity;

    public FormVersionEntity getFormVersionEntity() {
        return formVersionEntity;
    }

    public T getAttachmentEntity() {
        return attachmentEntity;
    }

    @Override
    public FormAttachmentEntityId getCod() {
        return cod;
    }

    public void setCod(FormAttachmentEntityId cod) {
        this.cod = cod;
    }

}