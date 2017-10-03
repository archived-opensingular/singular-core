package org.opensingular.form.persistence.dto;

import org.opensingular.form.SInstance;
import org.opensingular.form.persistence.entity.FormEntity;

public class InstanceFormDTO {

    private SInstance instance;
    private FormEntity form;

    public SInstance getInstance() {
        return instance;
    }

    public void setInstance(SInstance instance) {
        this.instance = instance;
    }

    public FormEntity getForm() {
        return form;
    }

    public void setForm(FormEntity form) {
        this.form = form;
    }
}
