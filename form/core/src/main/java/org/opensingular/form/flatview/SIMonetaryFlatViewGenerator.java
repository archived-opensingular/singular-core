package org.opensingular.form.flatview;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
import org.opensingular.lib.commons.canvas.FormItem;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public class SIMonetaryFlatViewGenerator extends AbstractFlatViewGenerator {

    private static final int DEFAULT_DIGITS = 2;

    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SInstance instance = context.getInstance();
        canvas.addFormItem(new FormItem(instance.asAtr().getLabel(),
                format(instance), instance.asAtrBootstrap().getColPreference()));
    }

    public String format(SInstance instance) {

        if ((instance != null) && (instance.getValue() != null)) {

            final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("pt", "BR"));
            final DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
            final BigDecimal valor = (BigDecimal) instance.getValue();
            final Integer digitos = getDecimalMaximo(instance);
            final StringBuilder pattern = new StringBuilder();

            pattern.append("R$ ###,###.");

            for (int i = 0; i < digitos; i += 1) {
                pattern.append('#');
            }

            decimalFormat.applyPattern(pattern.toString());
            decimalFormat.setMinimumFractionDigits(digitos);

            return decimalFormat.format(valor);
        }

        return StringUtils.EMPTY;
    }

    private Integer getDecimalMaximo(SInstance instance) {
        Optional<Integer> decimalMaximo = Optional.ofNullable(
                instance.getAttributeValue(SPackageBasic.ATR_FRACTIONAL_MAX_LENGTH));
        return decimalMaximo.orElse(DEFAULT_DIGITS);
    }

}