package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DecimalMapper extends StringMapper {

    private static final int DEFAULT_INTEGER_DIGITS = 9;
    private static final int DEFAULT_DIGITS = 2;

    @Override
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        TextField<BigDecimal> comp = new TextField<>(model.getObject().getNome(),
                new MInstanciaValorModel<>(model), BigDecimal.class);
        formGroup.appendInputText(comp.setLabel(labelModel).setOutputMarkupId(true)
                .add(new InputMaskBehavior(withOptionsOf(model), true)));
        return comp;
    }

    private Map<String, Object> withOptionsOf(IModel<? extends MInstancia> model) {
        Optional<Integer> inteiroMaximo = Optional.ofNullable(
                model.getObject().getValorAtributo(MPacoteBasic.ATR_TAMANHO_INTEIRO_MAXIMO));
        Optional<Integer> decimalMaximo = Optional.ofNullable(
                model.getObject().getValorAtributo(MPacoteBasic.ATR_TAMANHO_DECIMAL_MAXIMO));
        Map<String, Object> options = defaultOptions();
        options.put("integerDigits", inteiroMaximo.orElse(DEFAULT_INTEGER_DIGITS));
        options.put("digits", decimalMaximo.orElse(DEFAULT_DIGITS));
        return options;
    }

    private Map<String, Object> defaultOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("alias", "decimal");
        options.put("radixPoint", ",");
        options.put("groupSeparator", ".");
        options.put("autoGroup", true);
        options.put("digitsOptional", true);

        return options;
    }
}
