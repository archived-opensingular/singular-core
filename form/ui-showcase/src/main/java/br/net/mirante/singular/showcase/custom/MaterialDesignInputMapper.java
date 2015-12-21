package br.net.mirante.singular.showcase.custom;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class MaterialDesignInputMapper implements IWicketComponentMapper {

    @Override
    public void buildForEdit(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {

        final BSControls formGroup = createFromGroup(ctx);

        formGroup.appendInputText(buildTextField(model));
        formGroup.appendLabel(buildLabel(model));
        formGroup.add(new AttributeAppender("class", " form-md-line-input form-md-floating-label"));
    }

    @Override
    public void buildForView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {

        final BSControls formGroup = createFromGroup(ctx);
        final MInstancia mi = model.getObject();

        formGroup.appendLabel(buildLabel(model));
        formGroup.appendTag("div", new BOutputPanel(mi.getNome(), getOutputString(mi)));
    }

    private IModel<String> getOutputString(MInstancia mi) {
        if (mi.getValor() != null) {
            return Model.of(String.valueOf(mi.getValor()));
        } else {
            return Model.of(StringUtils.EMPTY);
        }
    }

    private BSLabel buildLabel(IModel<? extends MInstancia> model) {
        final AtributoModel<String> labelModel = new AtributoModel<>(model, MPacoteBasic.ATR_LABEL);
        return new BSLabel("label", labelModel);
    }

    private TextField buildTextField(IModel<? extends MInstancia> model) {
        final MInstancia mi = model.getObject();
        return new TextField<>(mi.getNome(), new MInstanciaValorModel<>(model), String.class);
    }

    private BSControls createFromGroup(WicketBuildContext ctx) {
        return ctx.getContainer().newFormGroup();
    }

}
