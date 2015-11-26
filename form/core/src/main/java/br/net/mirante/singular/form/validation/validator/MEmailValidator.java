package br.net.mirante.singular.form.validation.validator;

import java.util.regex.Pattern;

import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.validation.IInstanceValidatable;
import br.net.mirante.singular.form.validation.IInstanceValidator;

public class MEmailValidator implements IInstanceValidator<MIString> {

    private static final MEmailValidator INSTANCE = new MEmailValidator();

    public static MEmailValidator getInstance() {
        return INSTANCE;
    }

    protected MEmailValidator() {
        /* COSNTRUTOR VAZIO */
    }

    @Override
    public void validate(IInstanceValidatable<MIString> validatable) {
        String value = validatable.getInstance().getValor();
        if (value == null)
            return;

        // versão *simplificada* da regex, copiada do validador do wicket 
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z]{2,}){1}$)");
        if (!pattern.matcher(value).matches()) {
            validatable.error("Email inválido");
        }
    }
}
