package br.net.mirante.singular.form.validation.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.validation.IInstanceValidatable;

public enum MTelefoneNacionalValidator implements IInstanceValueValidator<MIString, String> {

    INSTANCE();

    public static final Pattern VALIDATE_PATTERN = Pattern.compile("\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}");

    @Override
    public void validate(IInstanceValidatable<MIString> validatable, String value) {
        final Matcher matcher = VALIDATE_PATTERN.matcher(value);
        if (!matcher.find()) {
            validatable.error("Número de telefone invalido");
        }
    }
}
