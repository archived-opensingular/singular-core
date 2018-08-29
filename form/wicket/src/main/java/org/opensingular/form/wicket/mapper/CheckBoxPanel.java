/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.mapper;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.view.SViewCheckBox;
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
        if (ctx.getView() != null && ctx.getView() instanceof SViewCheckBox) {
            alignment = ((SViewCheckBox) ctx.getView()).getAlignmentOfLabel();
        }
        String style = "";
        if (alignment != null) {
            style = "text-align:" + alignment.name().toLowerCase() + "";
        }
        return style;
    }

}
