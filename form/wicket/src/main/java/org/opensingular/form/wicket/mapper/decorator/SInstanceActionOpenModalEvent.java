/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.wicket.mapper.decorator;

import static java.util.stream.Collectors.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.SInstanceAction;
import org.opensingular.form.decorator.action.SInstanceAction.ActionHandler;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.ButtonStyle;
import org.opensingular.lib.wicket.util.modal.IOpenModalEvent;
import org.opensingular.lib.wicket.util.model.IMappingModel;

/**
 * ESTA CLASSE DE EVENTO NÃO É SERIALIZÁVEL!!!
 * Por isso a classe de botão é estática, para manter o controle das referências. Cuidado com referências implícitas!
 */
final class SInstanceActionOpenModalEvent implements IOpenModalEvent {
    private String                                     title;
    private AjaxRequestTarget                          target;
    private IModel<? extends Serializable>             textModel;
    private IModel<? extends SInstance>                instanceModel;
    private IModel<? extends SInstance>                formInstanceModel;
    private ISupplier<? extends List<SInstanceAction>> actions;

    public SInstanceActionOpenModalEvent(String title,
            AjaxRequestTarget target,
            IModel<? extends Serializable> textModel,
            IModel<? extends SInstance> instanceModel,
            IModel<? extends SInstance> formInstanceModel,
            ISupplier<? extends List<SInstanceAction>> actions) {
        this.title = title;
        this.target = target;
        this.textModel = textModel;
        this.instanceModel = instanceModel;
        this.formInstanceModel = formInstanceModel;
        this.actions = actions;
    }

    @Override
    public AjaxRequestTarget getTarget() {
        return this.target;
    }

    @Override
    public Component getBodyContent(String id) {

        Component textPanel = (textModel != null)
            ? new Label("textPanel", IMappingModel.of(textModel)
                .map(it -> it.toString()))
                    .setEscapeModelStrings(false)
                    .add($b.visibleIf($m.isNotNullOrEmpty(this.textModel)))
            : new WebMarkupContainer("textPanel");

        Component formPanel = (formInstanceModel != null)
            ? new SingularFormPanel("formPanel", true)
                .setInstanceCreator(new ModelGetterSupplier<SInstance>(formInstanceModel))
                .add($b.visibleIf($m.isNotNullOrEmpty(this.formInstanceModel)))
            : new WebMarkupContainer("formPanel");

        return new TemplatePanel(id, ""
            + "<div wicket:id='textPanel'></div>"
            + "<div wicket:id='formPanel'></div>")
                .add(textPanel)
                .add(formPanel)
                .setDefaultModel((formInstanceModel != null) ? formInstanceModel : $m.ofValue());
    }
    @Override
    public void configureModal(BSModalBorder modal) {
        modal.setTitleText(Model.of(this.title));
        
        List<SInstanceAction> actionsList = actions.get();
        for (int i = 0; i < actionsList.size(); i++) {
            final SInstanceAction action = actionsList.get(i);
            modal.addButton(
                resolveButtonStyle(action.getType()),
                Model.of(action.getText()),
                new FooterButton("action" + i, action, instanceModel, formInstanceModel));
        }
    }

    private static ButtonStyle resolveButtonStyle(SInstanceAction.ActionType actionType) {
        switch (actionType) {
            case PRIMARY:
                return ButtonStyle.PRIMARY;
            case LINK:
                return ButtonStyle.LINK;
            case DANGER:
                return ButtonStyle.DANGER;
            case CONFIRM:
                return ButtonStyle.CONFIRM;
            case CANCEL:
                return ButtonStyle.CANCEL;
            case NORMAL:
            default:
                return ButtonStyle.DEFAULT;
        }
    }

    static final class FooterButton extends ActionAjaxButton {

        private final IModel<? extends SInstance> instanceSupplier;
        private final IModel<? extends SInstance> formInstanceModel;
        private final SInstanceAction             action;

        private FooterButton(String id,
                SInstanceAction action,
                IModel<? extends SInstance> instanceSupplier,
                IModel<? extends SInstance> formInstanceModel) {
            super(id);
            this.action = action;
            this.instanceSupplier = instanceSupplier;
            this.formInstanceModel = formInstanceModel;
        }
        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            ActionHandler actionHandler = action.getActionHandler();
            if (actionHandler != null) {
                List<Object> childContextList = Arrays.asList(
                    target,
                    form,
                    formInstanceModel,
                    (formInstanceModel == null) ? null : formInstanceModel.getObject(),
                    this)
                    .stream()
                    .filter(it -> it != null)
                    .collect(toList());
                actionHandler.onAction(
                    action,
                    new ModelGetterSupplier<SInstance>(formInstanceModel),
                    new WicketSIconActionDelegate(
                        instanceSupplier,
                        childContextList));
            }
        }
    }

    static final class ModelGetterSupplier<T> implements ISupplier<T> {
        private IModel<? extends T> model;
        public ModelGetterSupplier(IModel<? extends T> model) {
            this.model = model;
        }
        @Override
        public T get() {
            return (model == null) ? null : model.getObject();
        }
    }
}