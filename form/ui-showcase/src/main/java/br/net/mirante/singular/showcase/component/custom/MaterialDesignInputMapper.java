package br.net.mirante.singular.showcase.component.custom;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
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
    public void buildView(WicketBuildContext ctx) {

        final IModel<? extends MInstancia> model = ctx.getModel();
        final BSControls formGroup = ctx.getContainer().newFormGroup();
        final MInstancia mi = ctx.getCurrenttInstance();
        final BSLabel label = new BSLabel("label", new AtributoModel<>(model, MPacoteBasic.ATR_LABEL));

        if(ctx.getViewMode().isVisualization()){
            formGroup.appendLabel(label);
            formGroup.appendTag("div", new BOutputPanel(mi.getNome(), getOutputString(mi)));
        } else {
            formGroup.appendInputText(new TextField<>(mi.getNome(), new MInstanciaValorModel<>(model)));
            formGroup.appendLabel(label);
            formGroup.add(new AttributeAppender("class", " form-md-line-input form-md-floating-label"));
        }
    }

    private IModel<String> getOutputString(MInstancia mi) {
        if (mi.getValor() != null) {
            return Model.of(String.valueOf(mi.getValor()));
        } else {
            return Model.of(StringUtils.EMPTY);
        }
    }

}