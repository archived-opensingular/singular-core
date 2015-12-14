package br.net.mirante.singular.form.wicket.mapper;

import java.util.HashMap;
import java.util.Optional;

import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

import static br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior.Masks;

public class IntegerMapper extends StringMapper {

    private static final int DEFAULT_SIZE = 9;

    @Override
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        Optional<Integer> size = Optional.ofNullable(
                model.getObject().getValorAtributo(MPacoteBasic.ATR_TAMANHO_MAXIMO));
        TextField<Integer> comp = new TextField<>(model.getObject().getNome(),
                new MInstanciaValorModel<>(model), Integer.class);
        formGroup.appendInputText(comp.setLabel(labelModel).setOutputMarkupId(true)
                .add(new InputMaskBehavior(Masks.NUMERIC, new HashMap<String, Object>() {{
                    put(InputMaskBehavior.MAX_LENGTH_ATTR, size.orElse(DEFAULT_SIZE));
                }})));
        return comp;
    }
}
