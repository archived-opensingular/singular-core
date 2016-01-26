package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
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
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.ErrorLevelFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel);

    String getReadOnlyFormattedText(IModel<? extends MInstancia> model);

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
                                          IModel<? extends MInstancia> model, IModel<String> labelModel) {
        final MInstancia mi = model.getObject();
        BOutputPanel comp = new BOutputPanel(mi.getNome(),
                $m.ofValue(getReadOnlyFormattedText(model)));
        formGroup.appendTag("div", comp);
        return comp;
    }

    default void buildView(WicketBuildContext ctx) {

        final IModel<? extends MInstancia> model = ctx.getModel();
        final boolean hintNoDecoration = ctx.getHint(NO_DECORATION);

        final IFeedbackMessageFilter feedbackMessageFilter = new ErrorLevelFeedbackMessageFilter(FeedbackMessage.WARNING);

        final BSContainer<?> container = ctx.getContainer();
        final BSControls controls = container.newFormGroup();

        final AtributoModel<String> labelModel = new AtributoModel<>(model, MPacoteBasic.ATR_LABEL);
        final AtributoModel<String> subtitle = new AtributoModel<>(model, MPacoteBasic.ATR_SUBTITLE);

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

            input.add($b.onConfigure(c -> {
                label.add(new ClassAttributeModifier() {
                    @Override
                    protected Set<String> update(Set<String> oldClasses) {
                        if (model.getObject().getValorAtributo(MPacoteCore.ATR_OBRIGATORIO)) {
                            oldClasses.add("required");
                        } else {
                            oldClasses.remove("required");
                        }
                        return oldClasses;
                    }
                });
            }));

            for (FormComponent fc : findAjaxComponents(input)) {
                ctx.configure(this, fc);
            }

        } else {
            input = appendReadOnlyInput(view, ctx.getExternalContainer(), controls, model, labelModel);
        }


        if ((input instanceof LabeledWebMarkupContainer) && (((LabeledWebMarkupContainer) input).getLabel() == null)) {
            ((LabeledWebMarkupContainer) input).setLabel(labelModel);
        }
    }


    default public FormComponent[] findAjaxComponents(Component input) {
        if (input instanceof FormComponent) {
            return new FormComponent[]{(FormComponent) input};
        } else if (input instanceof MarkupContainer) {
            List<FormComponent> formComponents = new ArrayList<>();
            ((MarkupContainer) input).visitChildren(new IVisitor<Component, Object>() {
                @Override
                public void component(Component component, IVisit<Object> iVisit) {
                    if (component instanceof FormComponent) {
                        formComponents.add((FormComponent) component);
                        iVisit.dontGoDeeper();
                    }
                }
            });
            return formComponents.toArray(new FormComponent[0]);
        } else {
            return new FormComponent[0];
        }

    }
}