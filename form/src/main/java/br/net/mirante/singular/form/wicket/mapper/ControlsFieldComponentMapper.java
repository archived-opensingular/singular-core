package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.behavior.InvisibleIfNullOrEmptyBehavior;
import br.net.mirante.singular.form.wicket.behavior.RequiredByTipoBehavior;
import br.net.mirante.singular.form.wicket.behavior.RequiredLabelIndicatorBehavior;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;

public interface ControlsFieldComponentMapper extends IWicketComponentMapper {

    static HintKey<Boolean> NO_DECORATION   = () -> false;
    static HintKey<Boolean> MATERIAL_DESIGN = () -> false;

    Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel);

    @Override
    default void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
        final boolean hintNoDecoration = ctx.getHint(NO_DECORATION);
        final boolean hintMaterialDesign = ctx.getHint(MATERIAL_DESIGN);

        final BSControls controls = ctx.getContainer().newFormGroup();

        final AtributoModel<String> labelModel = new AtributoModel<>(model, MPacoteBasic.ATR_LABEL);
        final AtributoModel<String> subtitle = new AtributoModel<>(model, MPacoteBasic.ATR_SUBTITLE);
        final AtributoModel<Integer> size = new AtributoModel<>(model, MPacoteBasic.ATR_TAMANHO_EDICAO);

        final BSLabel label = new BSLabel("label", labelModel);
        label.add(DisabledClassBehavior.INSTANCE);
        if (hintNoDecoration) {
            label.add($b.classAppender("visible-sm visible-xs"));
        }

        final Component input;
        if (hintMaterialDesign) {
            input = appendInput(controls, model, labelModel);
            controls.appendLabel(label);
            controls.newHelpBlock(subtitle).add(InvisibleIfNullOrEmptyBehavior.INSTANCE);
        } else {
            controls.appendLabel(label);
            controls.newHelpBlock(subtitle).add(InvisibleIfNullOrEmptyBehavior.INSTANCE);
            input = appendInput(controls, model, labelModel);
        }
        input.add(DisabledClassBehavior.INSTANCE);
        if (input instanceof FormComponent<?>) {
            FormComponent<?> fcInput = (FormComponent<?>) input;
            fcInput.add(RequiredByTipoBehavior.INSTANCE);
            label.add(RequiredLabelIndicatorBehavior.INSTANCE);
        }

        if (input instanceof TextField<?>) {
            input.add($b.attr("size", size, size.emptyModel().not()));
        }
    }
}