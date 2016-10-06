package br.net.mirante.singular.server.commons.wicket.view.form;

import org.opensingular.singular.form.SType;

import java.io.Serializable;

public class TransitionConfirmConfig implements Serializable {

    private Class<? extends SType<?>> type;
    private boolean                   validatePageForm;

    public TransitionConfirmConfig() {
        this.validatePageForm = true;
    }

    public Class<? extends SType<?>> getType() {
        return type;
    }

    public TransitionConfirmConfig setType(Class<? extends SType<?>> type) {
        this.type = type;
        return this;
    }

    public boolean isValidatePageForm() {
        return validatePageForm;
    }

    public TransitionConfirmConfig setValidatePageForm(boolean validatePageForm) {
        this.validatePageForm = validatePageForm;
        return this;
    }
}
