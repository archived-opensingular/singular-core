package br.net.mirante.singular.form.validation.validator;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.net.mirante.singular.form.validation.IValidatable;
import br.net.mirante.singular.form.validation.IValidator;

public class MCPFValidator implements IValidator<String> {

    private static final Logger LOGGER = Logger.getLogger("MCPFValidator");

    private List<String> invalidPatterns = Arrays.asList(
        "00000000000", "11111111111", "22222222222", "33333333333", "44444444444",
        "55555555555", "66666666666", "77777777777", "88888888888", "99999999999");

    private static final MCPFValidator INSTANCE = new MCPFValidator();

    public static MCPFValidator getInstance() {
        return INSTANCE;
    }

    protected MCPFValidator() {
        /* COSNTRUTOR VAZIO */
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String value = validatable.getValue();
        if (!isValid(value)) {
            validatable.error("CPF inválido");
        }
    }

    private boolean isValid(String cpf) {
        try {
            if (invalidPatterns.contains(cpf)) {
                return false;
            }

            if (cpf.trim().length() != 11) {
                return false;
            }

            int i;
            int d1 = 0;
            String cpf1 = cpf.substring(0, 9);
            String cpf2 = cpf.substring(9);

            for (i = 0; i < 9; i++) {
                d1 += Integer.parseInt(cpf1.substring(i, i + 1)) * (10 - i);
            }

            d1 = 11 - (d1 % 11);
            if (d1 > 9) {
                d1 = 0;
            }

            if (Integer.parseInt(cpf2.substring(0, 1)) != d1) {
                return false;
            }

            d1 *= 2;
            for (i = 0; i < 9; i++) {
                d1 += Integer.parseInt(cpf1.substring(i, i + 1)) * (11 - i);
            }

            d1 = 11 - (d1 % 11);
            if (d1 > 9) {
                d1 = 0;
            }

            return Integer.parseInt(cpf2.substring(1, 2)) == d1;
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }

        return false;
    }
}
