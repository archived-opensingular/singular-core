package br.net.mirante.singular.form.validation.validator;

import java.util.regex.Pattern;

import org.apache.wicket.validation.validator.EmailAddressValidator;

import br.net.mirante.singular.form.validation.IValidatable;
import br.net.mirante.singular.form.validation.IValidator;

public class MEmailValidator implements IValidator<String> {
    private static final MEmailValidator INSTANCE = new MEmailValidator();
    public static MEmailValidator getInstance() {
        return INSTANCE;
    }

    protected MEmailValidator() {}

    @Override
    public void validate(IValidatable<String> validatable) {
        Pattern pattern = EmailAddressValidator.getInstance().getPattern();
        String value = validatable.getValue();
        if (!pattern.matcher(value).matches()) {
            validatable.error("Email inválido");
        }
    }
}
