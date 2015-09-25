package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;
import static org.apache.commons.lang3.StringUtils.*;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import br.net.mirante.singular.util.wicket.model.ValueModel;

public interface ControlsFieldComponentMapper extends IWicketComponentMapper {

    public static HintKey<Boolean> NO_DECORATION = () -> false;

    public Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel);

    @Override
    public default void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
        final boolean hintNoDecoration = ctx.getHint(NO_DECORATION);

        MInstancia instancia = model.getObject();
        String label = trimToEmpty(instancia.as(MPacoteBasic.aspect()).getLabel());
        ValueModel<String> labelModel = $m.ofValue(label);
        BSControls controls = ctx.getContainer().newFormGroup();

        BSLabel bsLabel = new BSLabel("label", labelModel);
        if (hintNoDecoration)
            bsLabel.add($b.classAppender("visible-sm visible-xs"));

        controls.appendLabel(bsLabel);
        Component comp = appendInput(controls, model, labelModel);
        controls.appendFeedback();

        Integer size = instancia.as(MPacoteBasic.aspect()).getTamanhoEdicao();
        if ((comp instanceof TextField<?>) && (size != null)) {
            comp.add($b.attr("size", size));
        }
    }
}