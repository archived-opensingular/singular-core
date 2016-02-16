package br.net.mirante.singular.form.wicket.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSWellBorder;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

public class BooleanMapper implements IWicketComponentMapper {

    public void buildView(WicketBuildContext ctx) {

        final IModel<? extends SInstance> model = ctx.getModel();
        final BSControls formGroup = ctx.getContainer().newComponent(BSControls::new);
        final AtributoModel<String> labelModel = new AtributoModel<>(model, SPackageBasic.ATR_LABEL);

        IModel<String> labelText = WicketUtils.$m.ofValue("");
        BSLabel label = new BSLabel("label", labelText);
        label.add(DisabledClassBehavior.getInstance());
        formGroup.appendLabel(label);

        switch (ctx.getViewMode()) {
            case VISUALIZATION:
                buildForVisualization(model, formGroup, labelModel, label, labelText);
                break;
            case EDITION:
                buildForEdition(ctx, model, formGroup, labelModel);
                break;
        }
    }

    private void buildForEdition(WicketBuildContext ctx, IModel<? extends SInstance> model, BSControls formGroup,
                                 AtributoModel<String> labelModel) {
        final CheckBox input = new CheckBox(model.getObject().getNome(), new MInstanciaValorModel<>(model));
        formGroup.appendCheckbox(input, labelModel);
        input.add(DisabledClassBehavior.getInstance());
        ctx.configure(this, input);
    }

    private void buildForVisualization(IModel<? extends SInstance> model, BSControls formGroup,
                                       AtributoModel<String> labelModel, BSLabel label, IModel<String> labelText) {
        labelText.setObject("&zwnj;");
        label.setEscapeModelStrings(false);
        final Boolean checked;

        final SInstance mi = model.getObject();
        if ((mi != null) && (mi.getValue() != null)) {
            checked = (Boolean) mi.getValue();
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
