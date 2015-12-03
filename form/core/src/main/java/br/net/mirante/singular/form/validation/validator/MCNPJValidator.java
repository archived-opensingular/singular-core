package br.net.mirante.singular.form.validation.validator;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.validation.IInstanceValidatable;

public class MCNPJValidator extends AbstractValueValidator<MIString, String> {

    private static final Logger LOGGER = Logger.getLogger("MCNPJValidator");

    private List<String> invalidPatterns = Arrays.asList(
        "00000000000000", "11111111111111", "22222222222222", "33333333333333", "44444444444444",
        "55555555555555", "66666666666666", "77777777777777", "88888888888888", "99999999999999");

    private static final MCNPJValidator INSTANCE = new MCNPJValidator();

    public static MCNPJValidator getInstance() {
        return INSTANCE;
    }

    protected MCNPJValidator() {
        /* COSNTRUTOR VAZIO */
    }

    @Override
    public void validate(IInstanceValidatable<MIString> validatable, String value) {
        if (!isValid(value)) {
            validatable.error("CNPJ inválido");
        }
    }

    private boolean isValid(String cnpj) {
        try {
            if (invalidPatterns.contains(cnpj)) {
                return false;
            }

            if (cnpj.trim().length() != 14) {
                return false;
            }

            char cnpjArray[] = cnpj.toCharArray();

            Integer digit1 = this.retrieveDV(cnpjArray);
            Integer digit2 = this.retrieveDV(cnpjArray, digit1);

            String dvExpected = digit1.toString() + digit2.toString();
            String dv = cnpjArray[cnpjArray.length - 2] + "" + cnpjArray[cnpjArray.length - 1];

            return dv.equals(dvExpected);
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }

        return false;
    }

    private int retrieveDV(char[] cnpjArray) {
        int factor = 5;
        int sum = 0;
        for (int i = 0; i < cnpjArray.length - 2; i++) {
            char c = cnpjArray[i];
            sum += Integer.parseInt(String.valueOf(c)) * factor;
            if (i == 3) {
                factor = 9;
            } else {
                factor--;
            }
        }

        int value = (sum) % 11;
        int dv = 11 - value;
        if ((sum) % 11 < 2) {
            dv = 0;
        }

        return dv;
    }

    private int retrieveDV(char[] cnpjArray, int prevDV) {
        int factor = 6;
        int sum = 0;
        for (int i = 0; i < cnpjArray.length - 2; i++) {
            char c = cnpjArray[i];
            sum += Integer.parseInt(String.valueOf(c)) * factor;
            if (i == 4) {
                factor = 9;
            } else {
                factor--;
            }
        }

        sum += prevDV * 2;
        int value = (sum) % 11;
        int dv = 11 - value;
        if ((sum) % 11 < 2) {
            dv = 0;
        }

        return dv;
    }
}
