package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;
import static org.apache.commons.lang3.StringUtils.*;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.util.wicket.behavior.StatelessBehaviors;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import br.net.mirante.singular.util.wicket.model.ValueModel;

public interface ControlsFieldComponentMapper extends IWicketComponentMapper {

    static HintKey<Boolean> NO_DECORATION   = () -> false;
    static HintKey<Boolean> MATERIAL_DESIGN = () -> false;

    Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel);

    @Override
    default void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
        final boolean hintNoDecoration = ctx.getHint(NO_DECORATION);
        final boolean hintMaterialDesign = ctx.getHint(MATERIAL_DESIGN);

        final MInstancia instancia = model.getObject();
        final String labelText = trimToEmpty(instancia.as(MPacoteBasic.aspect()).getLabel());
        final ValueModel<String> labelModel = $m.ofValue(labelText);
        final BSControls controls = ctx.getContainer().newFormGroup();

        final BSLabel label = new BSLabel("label", labelModel);
        label.add(StatelessBehaviors.DISABLED_ATTR);
        if (hintNoDecoration) {
            label.add($b.classAppender("visible-sm visible-xs"));
        }

        final String subtitle = trimToEmpty(instancia.as(MPacoteBasic.aspect()).getSubtitle());

        final Component input;
        if (hintMaterialDesign) {
            input = appendInput(controls, model, labelModel);
            controls.appendLabel(label);
            if (!subtitle.isEmpty())
                controls.appendHelpBlock($m.ofValue(subtitle));
        } else {
            controls.appendLabel(label);
            if (!subtitle.isEmpty())
                controls.appendHelpBlock($m.ofValue(subtitle));
            input = appendInput(controls, model, labelModel);
        }
        input.add(StatelessBehaviors.DISABLED_ATTR);
        if (instancia.as(MPacoteBasic.aspect()).isObrigatorio() && (input instanceof FormComponent<?>)) {
            ((FormComponent<?>) input).setRequired(true);
            label.add(StatelessBehaviors.REQUIRED_AFTER);
        }

        final Integer size = instancia.as(MPacoteBasic.aspect()).getTamanhoEdicao();
        if ((input instanceof TextField<?>) && (size != null)) {
            input.add($b.attr("size", size));
        }
    }
}