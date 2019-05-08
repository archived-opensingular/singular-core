package org.opensingular.lib.commons.util.format;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class NumberFormatUtil {

    public static String formatValor(BigDecimal valor) {
        return Objects.nonNull(valor) ? NumberFormat.getCurrencyInstance(Locale.getDefault()).format(valor) : null;
    }

    public static String formatValorSemMoeda(BigDecimal valor) {
        return Objects.nonNull(valor) ? createNumberFormater(2).format(valor) : null;
    }

    // Converte uma String que representa um número no formato usado no Brasil ("." separador de milhar e "," separador de casas decimais)
    // para um BigDecimal válido
    public static BigDecimal formatDecimalSeparator(String value) {
        if (!StringUtils.isEmpty(value)) {
            return new BigDecimal(value.replaceAll("\\.", "").replaceAll(",", "."));
        }
        return BigDecimal.ZERO;
    }

    private static NumberFormat createNumberFormater(int exactFractionDigits) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(exactFractionDigits);
        numberFormat.setMinimumFractionDigits(exactFractionDigits);
        return numberFormat;
    }
}
