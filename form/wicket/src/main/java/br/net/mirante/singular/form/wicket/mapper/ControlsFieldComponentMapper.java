package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.ErrorLevelFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.behavior.InvisibleIfNullOrEmptyBehavior;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;

public interface ControlsFieldComponentMapper extends IWicketComponentMapper {

    HintKey<Boolean> NO_DECORATION = () -> false;

    Component appendInput(BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel);

    @Override
    default void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model) {
        final boolean hintNoDecoration = ctx.getHint(NO_DECORATION);

        final IFeedbackMessageFilter feedbackMessageFilter = new ErrorLevelFeedbackMessageFilter(FeedbackMessage.WARNING);

        final BSControls controls = ctx.getContainer().newFormGroup();

        final AtributoModel<String> labelModel = new AtributoModel<>(model, MPacoteBasic.ATR_LABEL);
        final AtributoModel<String> subtitle = new AtributoModel<>(model, MPacoteBasic.ATR_SUBTITLE);
        final AtributoModel<Integer> size = new AtributoModel<>(model, MPacoteBasic.ATR_TAMANHO_EDICAO);

        final BSLabel label = new BSLabel("label", labelModel);
        label.add(DisabledClassBehavior.getInstance());
        if (hintNoDecoration) {
            label.add($b.classAppender("visible-sm visible-xs"));
        }

        controls.appendLabel(label);
        controls.newHelpBlock(subtitle).add(InvisibleIfNullOrEmptyBehavior.getInstance());
        final Component input = appendInput(controls, model, labelModel);
        controls.appendFeedback(controls, feedbackMessageFilter);

        input.add(DisabledClassBehavior.getInstance());

        if (input instanceof FormComponent<?>) {
            ctx.configure((FormComponent<?>) input);
        }
        if (input instanceof LabeledWebMarkupContainer && ((LabeledWebMarkupContainer) input).getLabel() == null) {
            ((LabeledWebMarkupContainer) input).setLabel(labelModel);
        }

        if (input instanceof TextField<?>) {
            input.add($b.attr("size", size, size.emptyModel().not()));
        }
    }
}