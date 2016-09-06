package br.net.mirante.singular.form.wicket.mapper.richtext;

import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.mapper.StringMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

public class PortletRichTextMapper extends StringMapper {

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        return ctx.getContainer().newComponent(id -> new PortletRichTextPanel(id, ctx));
    }

    @Override
    protected Component appendReadOnlyInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final BOutputPanel outputPanel = (BOutputPanel) super.appendReadOnlyInput(ctx, formGroup, labelModel);
        outputPanel.getOutputTextLabel().setEscapeModelStrings(false);
        return outputPanel;
    }

    @Override
    protected void configureLabel(WicketBuildContext ctx, IModel<String> labelModel, boolean hintNoDecoration, BSLabel label) {
        label.setVisible(false);
    }

}