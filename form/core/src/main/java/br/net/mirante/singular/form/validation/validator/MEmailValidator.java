package br.net.mirante.singular.form.validation.validator;

import org.apache.commons.validator.routines.EmailValidator;

import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.validation.IInstanceValidatable;

public enum MEmailValidator implements IInstanceValueValidator<SIString, String> {

    /**
     * Local address is considered invalid
     */
    INSTANCE(false),
    /**
     * Local address is considered valid
     */
    INSTANCE_ALLOW_LOCAL_ADDRESS(true),
    ;
    
    private final boolean allowLocal;
    
    /**
     * @param allowLocal Should local addresses be considered valid?
     */
    private MEmailValidator(boolean allowLocal) {
        this.allowLocal = allowLocal;
    }

    @Override
    public void validate(IInstanceValidatable<SIString> validatable, String value) {
        if (!EmailValidator.getInstance(allowLocal).isValid(value)) {
            validatable.error("Email inválido");
        }
    }
}
