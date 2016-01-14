package br.net.mirante.singular.form.validation.validator;

import java.util.regex.Pattern;

import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.validation.IInstanceValidatable;

public enum MCEPValidator implements IInstanceValueValidator<MIString, String> {

    INSTANCE;

    @Override
    public void validate(IInstanceValidatable<MIString> validatable, String value) {
        if (!Pattern.matches("[0-9]{2}.[0-9]{3}-[0-9]{3}", value)) {
            validatable.error("CEP inválido");
        }
    }
}
