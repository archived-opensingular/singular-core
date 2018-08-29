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

package org.opensingular.form.wicket.mapper.richtext;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.StringMapper;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSLabel;

public class PortletRichTextMapper extends StringMapper {

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        return formGroup.newComponent(id -> new PortletRichTextPanel(id, ctx, false));
    }

    @Override
    protected Component appendReadOnlyInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        final SInstance mi = model.getObject();

        PortletRichTextPanel richTextPanel = new PortletRichTextPanel(mi.getName(), ctx, true);
        formGroup.appendTag("div", richTextPanel);

        return richTextPanel;
    }

    @Override
    public BSLabel createLabel(WicketBuildContext ctx) {
        return (BSLabel) super.createLabel(ctx).setVisible(false);
    }
}