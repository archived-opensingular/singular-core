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

package org.opensingular.studio.core.panel;

import de.alpharogroup.wicket.js.addon.toastr.ToastrType;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.component.SingularSaveButton;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.studio.core.definition.StudioDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CrudEditContent extends CrudShellContent {

    private SingularFormPanel singularFormPanel;
    private CrudShellContent previousContent;
    private List<ButtonFactory> buttonFactories = new ArrayList<>();
    private ISupplier<SInstance> instanceFactory;
    private ViewMode viewMode = ViewMode.EDIT;
    private ButtonFactory cancelButtonFactory = new CancelButtonFactory();
    private ButtonFactory saveButtonFactory = new SaveButtonFactory(getCrudShellManager());

    public CrudEditContent(CrudShellManager crudShellManager, CrudShellContent previousContent, IModel<SInstance> instance) {
        super(crudShellManager);
        this.previousContent = previousContent;
        this.instanceFactory = () -> instance != null ? instance.getObject() : getFormPersistence().createInstance();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addSingularFormPanel();
        addButtons();
    }

    private void addButtons() {
        buttonFactories.add(cancelButtonFactory);
        buttonFactories.add(saveButtonFactory);
        ListView<ButtonFactory> buttons = new ListView<ButtonFactory>("buttons", buttonFactories) {
            @Override
            protected void populateItem(ListItem<ButtonFactory> item) {
                ButtonFactory buttonFactory = item.getModelObject();
                Button button = buttonFactory.make("button", (IModel<SInstance>) singularFormPanel.getInstanceModel());
                button.add(new Label("label", buttonFactory.getLabel()));
                item.add(button);
            }
        };
        buttons.setRenderBodyOnly(true);
        add(buttons);
    }

    private void addSingularFormPanel() {
        singularFormPanel = new SingularFormPanel("singularFormPanel", instanceFactory);
        singularFormPanel.setViewMode(viewMode);
        add(singularFormPanel);
    }

    public CrudEditContent addButtonFactory(ButtonFactory buttonFactory) {
        buttonFactories.add(buttonFactory);
        return this;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setCancelButtonFactory(ButtonFactory cancelButtonFactory) {
        this.cancelButtonFactory = cancelButtonFactory;
    }

    public void setSaveButtonFactory(ButtonFactory saveButtonFactory) {
        this.saveButtonFactory = saveButtonFactory;
    }

    public interface ButtonFactory extends Serializable {

        Button make(String id, IModel<SInstance> instanceModel);

        String getLabel();

    }

    public class CancelButtonFactory implements ButtonFactory {

        @Override
        public Button make(String id, IModel<SInstance> instanceModel) {
            return new AjaxButton(id) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    super.onSubmit(target, form);
                    if (previousContent == null) {
                        CrudListContent crudListContent = getCrudShellManager().makeListContent();
                        getCrudShellManager().replaceContent(target, crudListContent);
                    } else {
                        getCrudShellManager().replaceContent(target, previousContent);
                    }
                }

                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new ClassAttributeModifier() {
                        @Override
                        protected Set<String> update(Set<String> oldClasses) {
                            oldClasses.add("cancel-btn");
                            return oldClasses;
                        }
                    });
                }
            };
        }

        @Override
        public String getLabel() {
            return "Cancelar";
        }
    }


    public static class SaveButtonFactory implements ButtonFactory {

        private final CrudShellManager crudShellManager;

        public SaveButtonFactory(CrudShellManager crudShellManager) {
            this.crudShellManager = crudShellManager;
        }

        @Override
        public Button make(String id, IModel<SInstance> instanceModel) {
            return new StudioSaveButton(id, instanceModel, crudShellManager);
        }

        @Override
        public String getLabel() {
            return "Salvar";
        }
    }

    public static class StudioSaveButton extends SingularSaveButton {

        private final CrudShellManager crudShellManager;

        public StudioSaveButton(String id, IModel<? extends SInstance> currentInstance, CrudShellManager crudShellManager) {
            super(id, currentInstance);
            this.crudShellManager = crudShellManager;
        }

        @Override
        protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
            StudioDefinition studioDefinition = crudShellManager.getStudioDefinition();
            studioDefinition.getRepository().insertOrUpdate(instanceModel.getObject(), null);
            crudShellManager.replaceContent(target, crudShellManager.makeListContent());
            crudShellManager.addToastrMessage(ToastrType.INFO, "Item salvo com sucesso.");
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();
            add(new ClassAttributeModifier() {
                @Override
                protected Set<String> update(Set<String> oldClasses) {
                    oldClasses.add("save-btn");
                    return oldClasses;
                }
            });
        }

        @Override
        protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
            super.onValidationError(target, form, instanceModel);
            crudShellManager.addToastrMessage(ToastrType.ERROR, "Existem correções a serem feitas no formulário.");
        }
    }

    protected IModel<? extends SInstance> getSingularFormPanelModelInstance() {
        return singularFormPanel.getInstanceModel();
    }

    protected SingularFormPanel getSingularFormPanel() {
        return singularFormPanel;
    }
}