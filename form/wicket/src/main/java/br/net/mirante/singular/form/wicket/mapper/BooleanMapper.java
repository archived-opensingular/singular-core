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
import br.net.mirante.singular.util.wicket.bootstrap.layout.*;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
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

        if (viewMode.isVisualization()) {
            buildForVisualization(model, formGroup, labelModel, label, labelText);
        } else {
            buildForEdition(model, formGroup, labelModel);
        }
    }

    private void buildForEdition(IModel<? extends MInstancia> model, BSControls formGroup,
                                 AtributoModel<String> labelModel) {
        final CheckBox input = new CheckBox(model.getObject().getNome(), new MInstanciaValorModel<>(model));
        formGroup.appendCheckbox(input, labelModel);
        input.add(DisabledClassBehavior.getInstance());
    }

    private void buildForVisualization(IModel<? extends MInstancia> model, BSControls formGroup,
                                       AtributoModel<String> labelModel, BSLabel label, IModel<String> labelText) {
        labelText.setObject("&zwnj;");
        label.setEscapeModelStrings(false);
        final Boolean checked;

        if (model.getObject() != null && model.getObject().getValor() != null) {
            checked = (Boolean) model.getObject().getValor();
        } else {
            checked = false;
        }

        String clazz = checked ? "fa fa-check-square" : "fa fa-square-o";
        String idSuffix = model.getObject().getNome();
        TemplatePanel tp = formGroup.newTemplateTag(t ->
                "<div wicket:id='" + "_well" + idSuffix + "'>"
                        + "   <i class='" + clazz + "'></i> " + labelModel.getObject()
                        + " </div>");
        tp.add(BSWellBorder.small("_well" + idSuffix));
    }
}
