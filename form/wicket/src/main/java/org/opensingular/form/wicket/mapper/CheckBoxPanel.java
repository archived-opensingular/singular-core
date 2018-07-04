package org.opensingular.form.wicket.mapper;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.view.SViewCheckBoxLabelAbove;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.AttributeModel;
import org.opensingular.lib.commons.ui.Alignment;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSWellBorder;

public class CheckBoxPanel extends Panel {


    public static final String BS_WELL = "_well";

    private final WicketBuildContext ctx;
    private final boolean showLabelInline;

    public CheckBoxPanel(String id, WicketBuildContext ctx, boolean showLabelInline) {
        super(id);
        this.ctx = ctx;
        this.showLabelInline = showLabelInline;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final IModel<? extends SInstance> model = ctx.getModel();
        final AttributeModel<String> labelModel = new AttributeModel<>(model, SPackageBasic.ATR_LABEL);
        final Boolean checked;

        final SInstance mi = model.getObject();
        if ((mi != null) && (mi.getValue() != null)) {
            checked = (Boolean) mi.getValue();
        } else {
            checked = Boolean.FALSE;
        }

        String clazz = checked ? "fa fa-check-square" : "fa fa-square-o";
        final BSWellBorder wellBorder = BSWellBorder.small("checkBoxPanel");
        wellBorder.add(new AttributeAppender("style", configureTextAlignStyle(ctx)));
        wellBorder.add(new WebMarkupContainer("checked").add(new AttributeAppender("class", clazz)));
        wellBorder.add(new Label("label", labelModel).setVisible(showLabelInline));
        add(wellBorder);

    }

    private String configureTextAlignStyle(WicketBuildContext ctx) {
        Alignment alignment = null;
        if (ctx.getView() != null && ctx.getView() instanceof SViewCheckBoxLabelAbove) {
            alignment = ((SViewCheckBoxLabelAbove) ctx.getView()).getAlignment();
        }
        String style = "";
        if (alignment != null) {
            style = "text-align:" + alignment.name().toLowerCase() + "";
        }
        return style;
    }

}
