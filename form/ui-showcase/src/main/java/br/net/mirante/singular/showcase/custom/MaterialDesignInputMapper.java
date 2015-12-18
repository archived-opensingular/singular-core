package br.net.mirante.singular.showcase.custom;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class MaterialDesignInputMapper implements IWicketComponentMapper {

    @Override
    public void buildView(UIBuilderWicket wicketBuilder, WicketBuildContext ctx,
                          MView view, IModel<? extends MInstancia> model, ViewMode viewMode) {

        final MInstancia mi = model.getObject();
        final BSContainer<?> container = ctx.getContainer();

        final BSControls formGroup = container.newFormGroup();
        final AtributoModel<String> labelModel = new AtributoModel<>(model, MPacoteBasic.ATR_LABEL);

        final BSLabel label = new BSLabel("label", labelModel);

        switch (viewMode) {
            case EDITION:
                final TextField<String> textField = new TextField<>(mi.getNome(), new MInstanciaValorModel<>(model), String.class);
                formGroup.appendInputText(textField);
                formGroup.appendLabel(label);
                formGroup.add($b.classAppender("form-md-line-input form-md-floating-label"));
                break;
            case VISUALIZATION:
                final String output;
                formGroup.appendLabel(label);
                if (mi.getValor() != null) {
                    output = String.valueOf(mi.getValor());
                } else {
                    output = StringUtils.EMPTY;
                }
                formGroup.appendTag("div", new BOutputPanel(mi.getNome(), Model.of(output)));
                break;
        }
    }

}
