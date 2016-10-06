/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.mapper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.singular.form.wicket.WicketBuildContext;
import org.opensingular.singular.form.wicket.behavior.MoneyMaskBehavior;
import org.opensingular.singular.form.wicket.model.SInstanceValueModel;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSControls;
import org.opensingular.singular.util.wicket.util.WicketUtils;

public class MoneyMapper extends AbstractControlsFieldComponentMapper {

    private static final int DEFAULT_INTEGER_DIGITS = 9;
    private static final int DEFAULT_DIGITS = 2;

    private static final String PRECISION = "precision";

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        Integer decimalMaximo = getDecimalMaximo(model);
        TextField<String> comp = new TextField<String>(model.getObject().getName(),
                new SInstanceValueModel<>(model), String.class) {
            @Override
            public IConverter getConverter(Class type) {
                return new MoneyConverter(decimalMaximo);
            }
        };

        formGroup.appendInputText(comp.setLabel(labelModel).setOutputMarkupId(true)
                .add(new Behavior() {
                    @Override
                    public void beforeRender(Component component) {
                        component.getResponse().write("<div class=\"input-group\">");
                        component.getResponse().write("<div class=\"input-group-addon\">R$</div>");
                    }

                    @Override
                    public void afterRender(Component component) {
                        component.getResponse().write("</div>");
                    }
                })
                .add(new MoneyMaskBehavior(withOptionsOf(model)))
                .add(WicketUtils.$b.attr("maxlength", calcularMaxLength(model))));

        return comp;
    }

    private Serializable calcularMaxLength(IModel<?extends SInstance> model) {
        Integer inteiro = getInteiroMaximo(model);
        Integer decimal = getDecimalMaximo(model);

        int tamanhoMascara = (int) Math.ceil((double)inteiro / 3);

        return inteiro + tamanhoMascara + decimal;
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();

        if ((mi != null) && (mi.getValue() != null)) {

            final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("pt", "BR"));
            final DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
            final BigDecimal valor = (BigDecimal) mi.getValue();
            final Map<String, Object> options = withOptionsOf(model);
            final Integer digitos = (int) options.get(PRECISION);
            final StringBuilder pattern = new StringBuilder();

            pattern.append("R$ ###,###.");

            for (int i = 0; i < digitos; i += 1) {
                pattern.append("#");
            }

            decimalFormat.applyPattern(pattern.toString());
            decimalFormat.setMinimumFractionDigits(digitos);

            return decimalFormat.format(valor);
        }

        return StringUtils.EMPTY;
    }

    private Map<String, Object> withOptionsOf(IModel<? extends SInstance> model) {
        Map<String, Object> options = defaultOptions();
        options.put(PRECISION, getDecimalMaximo(model));
        return options;
    }

    private Integer getDecimalMaximo(IModel<? extends SInstance> model) {
        Optional<Integer> decimalMaximo = Optional.ofNullable(
                model.getObject().getAttributeValue(SPackageBasic.ATR_FRACTIONAL_MAX_LENGTH));
        return decimalMaximo.orElse(DEFAULT_DIGITS);
    }

    private Integer getInteiroMaximo(IModel<? extends SInstance> model) {
        Optional<Integer> inteiroMaximo = Optional.ofNullable(
                model.getObject().getAttributeValue(SPackageBasic.ATR_INTEGER_MAX_LENGTH));
        return inteiroMaximo.orElse(DEFAULT_INTEGER_DIGITS);
    }

    private Map<String, Object> defaultOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("thousands", ".");
        options.put("decimal", ",");
        options.put("allowZero", false);
        options.put("allowNegative", true);

        return options;
    }

    private String formatDecimal(BigDecimal bigDecimal, Integer casasDecimais) {
        DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(new Locale("pt", "BR"));
        nf.setParseBigDecimal(true);
        nf.setGroupingUsed(true);
        nf.setMinimumFractionDigits(casasDecimais);
        nf.setMaximumFractionDigits(casasDecimais);
        return nf.format(bigDecimal);
    }

    private class MoneyConverter implements IConverter {
        private Integer casasDecimais;

        public MoneyConverter(Integer casasDecimais) {
            this.casasDecimais = casasDecimais;
        }

        @Override
        public Object convertToObject(String value, Locale locale) {
            if (!StringUtils.isEmpty(value)) {
                return new BigDecimal(value.replaceAll("\\.", "").replaceAll(",", "."));
            }

            return null;
        }

        @Override
        public String convertToString(Object value, Locale locale) {
            if (value == null) {
                return "";
            }else if (value instanceof String) {
                value = convertToObject((String) value, locale);
            }

            BigDecimal bigDecimal = (BigDecimal) value;
            return formatDecimal(bigDecimal.setScale(casasDecimais, BigDecimal.ROUND_HALF_UP), casasDecimais);
        }

    }
}
