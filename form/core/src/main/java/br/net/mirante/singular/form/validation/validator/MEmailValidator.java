package br.net.mirante.singular.form.validation.validator;

import java.util.regex.Pattern;

import br.net.mirante.singular.form.validation.IValueValidatable;
import br.net.mirante.singular.form.validation.IValueValidator;

public class MEmailValidator implements IValueValidator<String> {

    private static final MEmailValidator INSTANCE = new MEmailValidator();

    public static MEmailValidator getInstance() {
        return INSTANCE;
    }

    protected MEmailValidator() {
        /* COSNTRUTOR VAZIO */
    }

    @Override
    public void validate(IValueValidatable<String> validatable) {
        // versão *simplificada* da regex, copiada do validador do wicket 
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z]{2,}){1}$)");
        String value = validatable.getValue();
        if (!pattern.matcher(value).matches()) {
            validatable.error("Email inválido");
        }
    }
}
