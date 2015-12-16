package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSWellBorder;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;

public class BooleanMapper implements IWicketComponentMapper {

    @Override
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model, ViewMode viewMode) {
        final BSControls formGroup = ctx.getContainer().newComponent(BSControls::new);
        final AtributoModel<String> labelModel = new AtributoModel<>(model, MPacoteBasic.ATR_LABEL);

        IModel<String> labelText = WicketUtils.$m.ofValue("");
        BSLabel label = new BSLabel("label", labelText);
        label.add(DisabledClassBehavior.getInstance());
        formGroup.appendLabel(label);

        switch (viewMode) {
            case VISUALIZATION:
                buildForVisualization(model, formGroup, labelModel, label, labelText);
                break;
            case EDITION:
                buildForEdition(ctx, model, formGroup, labelModel);
                break;
        }
    }

    private void buildForEdition(WicketBuildContext ctx, IModel<? extends MInstancia> model, BSControls formGroup,
        AtributoModel<String> labelModel) {
        final CheckBox input = new CheckBox(model.getObject().getNome(), new MInstanciaValorModel<>(model));
        formGroup.appendCheckbox(input, labelModel);
        input.add(DisabledClassBehavior.getInstance());
        ctx.configure(input);
    }

    private void buildForVisualization(IModel<? extends MInstancia> model, BSControls formGroup,
        AtributoModel<String> labelModel, BSLabel label, IModel<String> labelText) {
        labelText.setObject("&zwnj;");
        label.setEscapeModelStrings(false);
        final Boolean checked;

        final MInstancia mi = model.getObject();
        if ((mi != null) && (mi.getValor() != null)) {
            checked = (Boolean) mi.getValor();
        } else {
            checked = false;
        }

        String clazz = checked ? "fa fa-check-square" : "fa fa-square-o";
        String idSuffix = (mi != null) ? mi.getNome() : StringUtils.EMPTY;
        TemplatePanel tp = formGroup.newTemplateTag(t -> ""
            + "<div wicket:id='" + "_well" + idSuffix + "'>"
            + "   <i class='" + clazz + "'></i> <span wicket:id='label'></span> "
            + " </div>");
        final BSWellBorder wellBorder = BSWellBorder.small("_well" + idSuffix);
        tp.add(wellBorder.add(new Label("label", labelModel.getObject())));
    }
}
