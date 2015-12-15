package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class DecimalMapper extends StringMapper {

    private static final int DEFAULT_INTEGER_DIGITS = 9;
    private static final int DEFAULT_DIGITS = 2;

    @Override
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        Integer decimalMaximo = getDecimalMaximo(model);
        TextField<String> comp = new TextField<String>(model.getObject().getNome(),
                new MInstanciaValorModel<>(model), String.class) {
            @Override
            public IConverter getConverter(Class type) {
                return new BigDecimalConverter(decimalMaximo);
            }
        };
        formGroup.appendInputText(comp.setLabel(labelModel).setOutputMarkupId(true)
                .add(new InputMaskBehavior(withOptionsOf(model), true)));
        return comp;
    }

    private Map<String, Object> withOptionsOf(IModel<? extends MInstancia> model) {
        Optional<Integer> inteiroMaximo = Optional.ofNullable(
                model.getObject().getValorAtributo(MPacoteBasic.ATR_TAMANHO_INTEIRO_MAXIMO));
        Integer decimal = getDecimalMaximo(model);
        Map<String, Object> options = defaultOptions();
        options.put("integerDigits", inteiroMaximo.orElse(DEFAULT_INTEGER_DIGITS));
        options.put("digits", decimal);
        return options;
    }

    private Integer getDecimalMaximo(IModel<? extends MInstancia> model) {
        Optional<Integer> decimalMaximo = Optional.ofNullable(
                model.getObject().getValorAtributo(MPacoteBasic.ATR_TAMANHO_DECIMAL_MAXIMO));
        return (Integer) decimalMaximo.orElse(DEFAULT_DIGITS);
    }

    private Map<String, Object> defaultOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("alias", "decimal");
        options.put("placeholder", "0");
        options.put("radixPoint", ",");
        options.put("groupSeparator", ".");
        options.put("autoGroup", true);
        options.put("digitsOptional", true);

        return options;
    }

    @SuppressWarnings("rawtypes")
    private class BigDecimalConverter implements IConverter {
        private Integer maximoCasasDecimais;

        public BigDecimalConverter(Integer maximoCasasDecimais) {
            this.maximoCasasDecimais = maximoCasasDecimais;
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
            int casasValue = bigDecimal.scale();
            int casasDecimais = casasValue < this.maximoCasasDecimais ? casasValue : this.maximoCasasDecimais;
            return bigDecimal.setScale(casasDecimais, BigDecimal.ROUND_HALF_UP).toString();
        }
    }
}
