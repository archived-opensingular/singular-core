package org.opensingular.form.wicket.mapper.decorator;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.SInstanceAction.Preview;
import org.opensingular.lib.commons.lambda.IFunction;

public class SInstanceActionPreviewPanel extends Panel {

    public SInstanceActionPreviewPanel(String id, IModel<Preview> previewModel,
        IModel<? extends SInstance> instanceModel,
        IFunction<AjaxRequestTarget, List<?>> internalContextListProvider) {
        super(id, previewModel);

        add($b.classAppender("singular-form-action-preview dropdown-menu theme-panel hold-on-click dropdown-custom"));
        add(new Label("title", $m.map(previewModel, it -> it.getTitle()))
            .add($b.visibleIfModelObject(it -> it != null)));
        add(new Label("previewText", $m.map(previewModel, it -> it.getMessage()))
            .setEscapeModelStrings(false));
        add(new SInstanceActionsPanel("actionsContainer",
            instanceModel,
            internalContextListProvider,
            SInstanceActionsPanel.Mode.BAR,
            $m.map(previewModel, it -> it.getActions())::getObject)
                .setActionClassFunction(it -> "singular-form-action-preview-action")
                .setLinkClassFunction(it -> "singular-form-action-preview-link"));
    }
}
