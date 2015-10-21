package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;

public class BooleanMapper implements IWicketComponentMapper {

    @Override
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
        final BSControls formGroup = ctx.getContainer().newComponent(BSControls::new);
        final AtributoModel<String> labelModel = new AtributoModel<>(model, MPacoteBasic.ATR_LABEL);
        final CheckBox input = new CheckBox(model.getObject().getNome(), new MInstanciaValorModel<>(model));

        formGroup.appendLabel(new BSLabel("label", "")
                .add(DisabledClassBehavior.getInstance()));
        formGroup.appendCheckbox(
                input,
                labelModel);

        input.add(DisabledClassBehavior.getInstance());
    }
}
