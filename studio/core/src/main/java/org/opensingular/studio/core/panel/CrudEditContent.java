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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CrudEditContent extends CrudShellContent {

    private SingularFormPanel singularFormPanel;
    private CrudShellContent previousContent;
    private Boolean showListOnSaveOrCancel = Boolean.TRUE;
    private List<ButtonFactory> buttonFactories = new ArrayList<>();
    private ISupplier<SInstance> instanceFactory;
    private ViewMode viewMode = ViewMode.EDIT;

    public CrudEditContent(CrudShellManager crudShellManager, CrudShellContent previousContent, IModel<SInstance> instance) {
        super(crudShellManager);
        this.previousContent = previousContent;
        this.instanceFactory = () -> instance != null ? instance.getObject() : getFormPersistence().createInstance();
        addDefaultButtons();
    }

    private void addDefaultButtons() {
        buttonFactories.add(new SaveButtonFactory());
        buttonFactories.add(new CancelButtonFactory());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addSingularFormPanel();
        addButtons();
    }

    private void addButtons() {
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

    public void setShowListOnSaveOrCancel(Boolean showListOnSaveOrCancel) {
        this.showListOnSaveOrCancel = showListOnSaveOrCancel;
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
                    if (Boolean.TRUE.equals(showListOnSaveOrCancel)) {
                        if (previousContent == null) {
                            getCrudShellManager().replaceContent(target, new CrudListContent(getCrudShellManager()));
                        } else {
                            getCrudShellManager().replaceContent(target, previousContent);
                        }
                    } else if (previousContent != null && !(previousContent instanceof CrudListContent)) {
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


    public class SaveButtonFactory implements ButtonFactory {

        @Override
        public Button make(String id, IModel<SInstance> instanceModel) {
            return new SingularSaveButton(id, instanceModel) {
                @Override
                protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                    getFormPersistence().insertOrUpdate(instanceModel.getObject(), null);
                    if (Boolean.TRUE.equals(showListOnSaveOrCancel)) {
                        getCrudShellManager().replaceContent(target, new CrudListContent(getCrudShellManager()));
                    }
                    getCrudShellManager().addToastrMessage(ToastrType.INFO, "Item salvo com sucesso.");
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
                    getCrudShellManager().addToastrMessage(ToastrType.ERROR, "Existem correções a serem feitas no formulário.");
                }
            };
        }

        @Override
        public String getLabel() {
            return "Salvar";
        }
    }
}