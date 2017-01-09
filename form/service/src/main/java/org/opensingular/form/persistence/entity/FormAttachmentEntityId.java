package org.opensingular.form.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FormAttachmentEntityId implements Serializable {

    @Column(name = "CO_VERSAO_FORMULARIO")
    private Long formVersionCod;

    @Column(name = "CO_ARQUIVO")
    private Long attachmentCod;

    public FormAttachmentEntityId() {
    }

    public FormAttachmentEntityId(Long formVersionCod, Long attachmentCod) {
        this.formVersionCod = formVersionCod;
        this.attachmentCod = attachmentCod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FormAttachmentEntityId that = (FormAttachmentEntityId) o;
        return Objects.equals(formVersionCod, that.formVersionCod) && Objects.equals(attachmentCod, that.attachmentCod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formVersionCod, attachmentCod);
    }
}
