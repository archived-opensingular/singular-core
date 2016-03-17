package br.net.mirante.singular.form.wicket.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.ErrorLevelFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.behavior.InvisibleIfNullOrEmptyBehavior;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;

public interface ControlsFieldComponentMapper extends IWicketComponentMapper {

    HintKey<Boolean> NO_DECORATION = () -> false;

    /**
     * @param view          Instancia da MView utilizada para configurar o componente
     * @param bodyContainer Container não aninhado no formulário, utilizado para adicionar modais por exemplo
     * @param formGroup     Container onde dever adicionado o input
     * @param model         Model da MInstancia
     * @param labelModel    Model contendo o label do componente
     * @return Retorna o componente  já adicionado ao formGroup
     */
    @SuppressWarnings("rawtypes")
    Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends SInstance> model, IModel<String> labelModel);

    String getReadOnlyFormattedText(IModel<? extends SInstance> model);

    /**
     * @param view          Instancia da MView utilizada para configurar o componente
     * @param bodyContainer Container não aninhado no formulário, utilizado para adicionar modais por exemplo
     * @param formGroup     Container onde dever adicionado o input
     * @param model         Model da MInstancia
     * @param labelModel    Model contendo o label do componentes
     * @return Retorna o componente  já adicionado ao formGroup
     */
    @SuppressWarnings("rawtypes")
    default Component appendReadOnlyInput(MView view, BSContainer bodyContainer, BSControls formGroup,
                                          IModel<? extends SInstance> model, IModel<String> labelModel) {
        final SInstance mi = model.getObject();
        final BOutputPanel comp = new BOutputPanel(mi.getNome(), $m.ofValue(getReadOnlyFormattedText(model)));
        formGroup.appendTag("div", comp);
        return comp;
    }

    default void buildView(WicketBuildContext ctx) {

        final IModel<? extends SInstance> model = ctx.getModel();
        final boolean hintNoDecoration = ctx.getHint(NO_DECORATION);

        final IFeedbackMessageFilter feedbackMessageFilter = new ErrorLevelFeedbackMessageFilter(FeedbackMessage.WARNING);

        final BSContainer<?> container = ctx.getContainer();
        final BSControls controls = container.newFormGroup();

        final AtributoModel<String> labelModel = new AtributoModel<>(model, SPackageBasic.ATR_LABEL);
        final AtributoModel<String> subtitle = new AtributoModel<>(model, SPackageBasic.ATR_SUBTITLE);

        final ViewMode viewMode = ctx.getViewMode();
        final MView view = ctx.getView();

        final BSLabel label = new BSLabel("label", labelModel);
        label.add(DisabledClassBehavior.getInstance());
        label.setVisible(!hintNoDecoration);

        controls.appendLabel(label);
        controls.newHelpBlock(subtitle)
                .add($b.classAppender("hidden-xs"))
                .add($b.classAppender("hidden-sm"))
                .add($b.classAppender("hidden-md"))
                .add(InvisibleIfNullOrEmptyBehavior.getInstance());

        final Component input;

        if (viewMode.isEdition()) {

            input = appendInput(view, ctx.getExternalContainer(), controls, model, labelModel);
            controls.appendFeedback(controls, feedbackMessageFilter);
            input.add(DisabledClassBehavior.getInstance());

            input.add($b.onConfigure(c -> label.add(new ClassAttributeModifier() {
                @Override
                protected Set<String> update(Set<String> oldClasses) {
                    if (model.getObject().getValorAtributo(SPackageCore.ATR_OBRIGATORIO)) {
                        oldClasses.add("singular-form-required");
                    } else {
                        oldClasses.remove("singular-form-required");
                    }
                    return oldClasses;
                }
            })));

            for (FormComponent fc : findAjaxComponents(input)) {
                ctx.configure(this, fc);
            }

        } else {
            input = appendReadOnlyInput(view, ctx.getExternalContainer(), controls, model, labelModel);
        }

        if(ctx.annotation().enabled()){
            if(input.getDefaultModel() != null){
                ctx.updateAnnotations(input, (SInstance) input.getDefaultModel().getObject());
            }
        }

        if ((input instanceof LabeledWebMarkupContainer) && (((LabeledWebMarkupContainer) input).getLabel() == null)) {
            ((LabeledWebMarkupContainer) input).setLabel(labelModel);
        }
    }


    default FormComponent[] findAjaxComponents(Component input) {
        if (input instanceof FormComponent) {
            return new FormComponent[]{(FormComponent) input};
        } else if (input instanceof MarkupContainer) {
            List<FormComponent> formComponents = new ArrayList<>();
            ((MarkupContainer) input).visitChildren((component, iVisit) -> {
                if (component instanceof FormComponent) {
                    formComponents.add((FormComponent) component);
                    iVisit.dontGoDeeper();
                }
            });
            return formComponents.toArray(new FormComponent[formComponents.size()]);
        } else {
            return new FormComponent[0];
        }

    }
}