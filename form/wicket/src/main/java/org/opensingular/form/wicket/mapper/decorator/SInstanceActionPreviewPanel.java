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

package org.opensingular.form.wicket.mapper.decorator;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.SInstanceAction;
import org.opensingular.form.decorator.action.SInstanceAction.Preview;
import org.opensingular.internal.form.wicket.util.HtmlConversionUtils;
import org.opensingular.lib.commons.lambda.IFunction;

public class SInstanceActionPreviewPanel extends Panel {

    public SInstanceActionPreviewPanel(String id, IModel<Preview> previewModel,
        IModel<? extends SInstance> instanceModel,
        IFunction<AjaxRequestTarget, List<?>> internalContextListProvider) {
        super(id, previewModel);

        IModel<String> messageModel = $m.map(previewModel,
            it -> HtmlConversionUtils.toHtmlMessage(it.getMessage(), it.getFormat()));
        IModel<List<SInstanceAction>> actionsModel = $m.map(previewModel, it -> it.getActions());

        add($b.classAppender("singular-form-action-preview dropdown-menu theme-panel hold-on-click dropdown-custom"));
        add(new Label("title", $m.map(previewModel, it -> it.getTitle()))
            .add($b.visibleIfModelObject(it -> it != null)));
        add(new Label("previewText", messageModel)
            .setEscapeModelStrings(false));
        add(new SInstanceActionsPanel("actionsContainer",
            instanceModel,
            internalContextListProvider,
            SInstanceActionsPanel.Mode.BAR,
            () -> actionsModel.getObject())
                .setActionClassFunction(it -> "singular-form-action-preview-action")
                .setLinkClassFunction(it -> "singular-form-action-preview-link"));
    }
}
