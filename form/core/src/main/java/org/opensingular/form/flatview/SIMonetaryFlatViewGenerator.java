/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
            final BigDecimal value = (BigDecimal) instance.getValue();
            final Integer digits = getDecimalMaximo(instance);
            final StringBuilder pattern = new StringBuilder();

            pattern.append("R$ ###,###.");

            for (int i = 0; i < digits; i += 1) {
                pattern.append('#');
            }

            decimalFormat.applyPattern(pattern.toString());
            decimalFormat.setMinimumFractionDigits(digits);

            return decimalFormat.format(value);
        }

        return StringUtils.EMPTY;
    }

    private Integer getDecimalMaximo(SInstance instance) {
        Optional<Integer> decimalMaximo = Optional.ofNullable(
                instance.getAttributeValue(SPackageBasic.ATR_FRACTIONAL_MAX_LENGTH));
        return decimalMaximo.orElse(DEFAULT_DIGITS);
    }

}