package org.opensingular.form.wicket.mapper;

import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.behavior.CKEditorInitBehaviour;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.output.BOutputPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class RichTextMapper extends StringMapper {

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        return appendTextarea(formGroup, createTextArea(labelModel, ctx.getModel()));
    }

    private Component appendTextarea(BSControls formGroup, Component textarea) {
        addLogicToReplaceWithCKEditor(textarea);
        formGroup.appendTextarea(textarea, 1);
        return textarea;
    }

    private Component createTextArea(IModel<String> labelModel, IModel<? extends SInstance> model) {
        return new TextArea<>(model.getObject().getName(), new SInstanceValueModel<>(model)).setLabel(labelModel);
    }

    private void addLogicToReplaceWithCKEditor(Component textarea) {
        textarea.add($b.attr("style", "display:none"));
        textarea.add(new CKEditorInitBehaviour());
    }

    @Override
    protected Component appendReadOnlyInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final BOutputPanel outputPanel = (BOutputPanel) super.appendReadOnlyInput(ctx, formGroup, labelModel);
        outputPanel.getOutputTextLabel().setEscapeModelStrings(false);
        return outputPanel;
    }

}