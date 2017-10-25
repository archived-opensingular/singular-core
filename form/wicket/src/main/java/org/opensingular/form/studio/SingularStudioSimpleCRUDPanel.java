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

package org.opensingular.form.studio;

import de.alpharogroup.wicket.js.addon.toastr.ToastrType;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.wicket.component.SingularSaveButton;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxLink;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.column.BSActionColumn;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.toastr.ToastrHelper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

/**
 * SingularStudioSimpleCRUDPanel permite renderizar um SType com todos os controles de crud
 *
 * @param <STYPE>    O SType a ser renderizado
 * @param <INSTANCE> O tipo de instancia do SType
 * @author ronaldtm
 * @author danilo.mesquita
 */
public abstract class SingularStudioSimpleCRUDPanel<STYPE extends SType<INSTANCE>, INSTANCE extends SIComposite>
        extends Panel {

    private static final String ID_CONTENT = "content";

    private final ISupplier<FormRespository<STYPE, INSTANCE>> formPersistence;

    private final WebMarkupContainer container = new WebMarkupContainer("container");

    private final ISupplier<StudioCRUDPermissionStrategy> studioCRUDPermissionStrategySupplier;

    private IModel<String> crudTitle = new Model<>();
    private IModel<Icon> crudIcon = new Model<>();

    public SingularStudioSimpleCRUDPanel(String id,
                                         FormRespository<STYPE, INSTANCE> formPersistence,
                                         ISupplier<StudioCRUDPermissionStrategy> studioCRUDPermissionStrategySupplier) {
        this(id, () -> formPersistence, studioCRUDPermissionStrategySupplier);
    }

    public SingularStudioSimpleCRUDPanel(String id,
                                         FormRespository<STYPE, INSTANCE> formPersistence) {
        this(id, () -> formPersistence);
    }

    public SingularStudioSimpleCRUDPanel(String id,
                                         ISupplier<FormRespository<STYPE, INSTANCE>> formPersistence) {
        this(id, formPersistence, () -> StudioCRUDPermissionStrategy.ALL);
    }

    public SingularStudioSimpleCRUDPanel(String id,
                                         ISupplier<FormRespository<STYPE, INSTANCE>> formPersistence,
                                         ISupplier<StudioCRUDPermissionStrategy> studioCRUDPermissionStrategySupplier) {
        super(id);
        this.formPersistence = formPersistence;
        this.studioCRUDPermissionStrategySupplier = studioCRUDPermissionStrategySupplier;
        add(container
                .setOutputMarkupId(true)
                .setOutputMarkupPlaceholderTag(true));
        showListContent(null);
    }


    public static FormKey getFormKey(SInstance ins) {
        return FormKey.from(ins);
    }

    protected FormRespository<STYPE, INSTANCE> getFormPersistence() {
        return this.formPersistence.get();
    }

    protected abstract void buildListTable(BSDataTableBuilder<INSTANCE, String, IColumn<INSTANCE, String>> dataTableBuilder);

    private void onEdit(AjaxRequestTarget target, IModel<INSTANCE> model) {
        showEditContent(target, getFormKey(model.getObject()));
    }

    private void onDelete(AjaxRequestTarget target, IModel<INSTANCE> model) {
        RemoveAjaxBehaviour removeAjaxAction = new RemoveAjaxBehaviour(model);
        add(removeAjaxAction);
        target.appendJavaScript("bootbox.confirm('Tem certeza que deseja excluir?', " +
                "function(isOk){if(isOk){Wicket.Ajax.get({u:'" + removeAjaxAction.getCallbackUrl() + "'});}});");
    }

    private void onView(AjaxRequestTarget target, IModel<INSTANCE> model) {
        showViewContent(target, getFormKey(model.getObject()));
    }

    private void onSave(AjaxRequestTarget target, IModel<INSTANCE> instanceModel) {
        INSTANCE instance = instanceModel.getObject();
        getFormPersistence().insertOrUpdate(instance, null);
        instanceModel.setObject(getFormPersistence().createInstance());
        showListContent(target);
        new ToastrHelper(SingularStudioSimpleCRUDPanel.this).addToastrMessage(ToastrType.INFO, "Item salvo com sucesso.");
    }

    private void onSaveCanceled(AjaxRequestTarget target) {
        showListContent(target);
    }

    protected final void replaceContent(AjaxRequestTarget target, Component content) {
        container.addOrReplace(content);
        if (target != null)
            target.add(container);
    }

    protected final void showListContent(AjaxRequestTarget target) {
        replaceContent(target, newListContent(ID_CONTENT));
    }

    protected final void showCreateContent(AjaxRequestTarget target) {
        replaceContent(target, newCreateContent(ID_CONTENT));
    }

    protected final void showEditContent(AjaxRequestTarget target, FormKey key) {
        replaceContent(target, newEditContent(ID_CONTENT, key));
    }

    protected final void showViewContent(AjaxRequestTarget target, FormKey key) {
        replaceContent(target, newViewContent(ID_CONTENT, key));
    }

    protected Component newCreateContent(String id) {
        return new FormFragment(id, getFormPersistence().createInstance(), ViewMode.EDIT);
    }

    protected Component newEditContent(String id, FormKey key) {
        return new FormFragment(id, getFormPersistence().load(key), ViewMode.EDIT);
    }

    protected Component newViewContent(String id, FormKey key) {
        return new FormFragment(id, getFormPersistence().load(key), ViewMode.READ_ONLY);
    }

    protected Component newListContent(String id) {
        return new ListFragment(id);
    }

    public SingularStudioSimpleCRUDPanel<STYPE, INSTANCE> setCrudTitle(String crudTitle) {
        this.crudTitle.setObject(crudTitle);
        return this;
    }

    public SingularStudioSimpleCRUDPanel<STYPE, INSTANCE> setCrudIcon(Icon crudIcon) {
        this.crudIcon.setObject(crudIcon);
        return this;
    }

    private class FormFragment extends Fragment {
        public FormFragment(String id, INSTANCE instance, ViewMode viewMode) {
            super(id, "FormFragment", SingularStudioSimpleCRUDPanel.this);
            Form<?> form = new Form<>("form");
            SingularFormPanel content = new SingularFormPanel(ID_CONTENT, instance);
            content.setViewMode(viewMode);
            add(form.add(content)
                    .add(new CRUDSaveButton(content))
                    .add(new ActionAjaxLink<Void>("cancel") {
                        @Override
                        protected void onAction(AjaxRequestTarget target) {
                            onSaveCanceled(target);
                        }
                    }));
        }
    }

    private class ListFragment extends Fragment {
        public ListFragment(String id) {
            super(id, "ListFragment", SingularStudioSimpleCRUDPanel.this);

            ISortableDataProvider<INSTANCE, String> dataProvider = new SortableDataProvider<INSTANCE, String>() {
                @Override
                public Iterator<? extends INSTANCE> iterator(long first, long count) {
                    return getFormPersistence().loadAll(first, count).iterator();
                }

                @Override
                public long size() {
                    return getFormPersistence().countAll();
                }

                @Override
                public IModel<INSTANCE> model(INSTANCE object) {
                    final FormKey key = getFormKey(object);
                    return $m.loadable(object, () -> getFormPersistence().load(key));
                }
            };

            WebMarkupContainer crudIconComponent = new WebMarkupContainer("crudIcon");
            crudIconComponent.add(new ClassAttributeModifier() {
                @Override
                protected Set<String> update(Set<String> oldClasses) {
                    Set<String> classes = new HashSet<>(oldClasses);
                    if (crudIcon.getObject() != null) {
                        classes.add(crudIcon.getObject().getCssClass());
                    }
                    return classes;
                }
            });

            Label crudTitleComponent = new Label("crudTitle", crudTitle);

            add(crudIconComponent, crudTitleComponent);

            BSDataTableBuilder<INSTANCE, String, IColumn<INSTANCE, String>> dataTableBuilder = new BSDataTableBuilder<>(dataProvider);
            buildListTable(dataTableBuilder);
            dataTableBuilder.appendActionColumn($m.ofValue(""), this::appendActions);
            dataTableBuilder.setBorderedTable(false);

            BSDataTable<INSTANCE, String> table = dataTableBuilder.build("table");
            table.add($b.classAppender("worklList"));

            add(table);
            StudioCRUDPermissionStrategy studioCRUDPermissionStrategy = studioCRUDPermissionStrategySupplier.get();
            if (studioCRUDPermissionStrategy == null || studioCRUDPermissionStrategy.canCreate()) {
                add(new ActionAjaxLink<Void>("create") {
                    @Override
                    protected void onAction(AjaxRequestTarget target) {
                        showCreateContent(target);
                    }
                });
            } else {
                add(new WebMarkupContainer("create").setVisible(false));
            }
        }

        private BSActionColumn<INSTANCE, String> appendActions(BSActionColumn<INSTANCE, String> col) {
            StudioCRUDPermissionStrategy studioCRUDPermissionStrategy = studioCRUDPermissionStrategySupplier.get();
            boolean isNull = studioCRUDPermissionStrategy == null;
            if (isNull || studioCRUDPermissionStrategy.canEdit()) {
                col.appendAction($m.ofValue("Editar"), DefaultIcons.PENCIL, SingularStudioSimpleCRUDPanel.this::onEdit);
            }
            if (isNull || studioCRUDPermissionStrategy.canRemove()) {
                col.appendAction($m.ofValue("Deletar"), DefaultIcons.TRASH, SingularStudioSimpleCRUDPanel.this::onDelete);
            }
            if (isNull || studioCRUDPermissionStrategy.canView()) {
                col.appendAction($m.ofValue("Visualizar"), DefaultIcons.EYE, SingularStudioSimpleCRUDPanel.this::onView);
            }
            return col;
        }
    }

    private class RemoveAjaxBehaviour extends AbstractDefaultAjaxBehavior {
        private final IModel<INSTANCE> model;

        private RemoveAjaxBehaviour(IModel<INSTANCE> model) {
            this.model = model;
        }

        @Override
        protected void respond(AjaxRequestTarget ajaxRequestTarget) {
            getFormPersistence().delete(getFormKey(model.getObject()));
            showListContent(ajaxRequestTarget);
            new ToastrHelper(SingularStudioSimpleCRUDPanel.this).addToastrMessage(ToastrType.INFO, "Item excluido com sucesso.");
        }
    }

    private class CRUDSaveButton extends SingularSaveButton {

        private SingularFormPanel content;

        public CRUDSaveButton(SingularFormPanel content) {
            super("save", content.getInstanceModel());
            this.content = content;
        }

        @Override
        protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
            onSave(target, (IModel<INSTANCE>) instanceModel);
        }

        @Override
        protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
            super.onValidationError(target, form, instanceModel);
            new ToastrHelper(SingularStudioSimpleCRUDPanel.this).addToastrMessage(ToastrType.ERROR, "Existem correções a serem feitas no formulário.");
        }

        @Override
        public boolean isVisible() {
            if (content.getViewMode().isVisualization()) {
                return false;
            } else {
                StudioCRUDPermissionStrategy studioCRUDPermissionStrategy = studioCRUDPermissionStrategySupplier.get();
                return studioCRUDPermissionStrategy.canCreate() || studioCRUDPermissionStrategy.canEdit();
            }
        }
    }
}
