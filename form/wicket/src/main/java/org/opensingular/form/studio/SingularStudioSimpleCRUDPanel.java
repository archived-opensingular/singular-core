package org.opensingular.form.studio;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormPersistence;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxLink;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.resource.Icone;

/**
 * Created by ronaldtm on 16/03/17.
 */
public abstract class SingularStudioSimpleCRUDPanel<T extends SType<I>, I extends SIComposite>
        extends Panel {

    private static final String ID_CONTENT = "content";

    private final ISupplier<FormPersistence<I>> formPersistence;

    private final WebMarkupContainer container = new WebMarkupContainer("container");

    public SingularStudioSimpleCRUDPanel(String id, FormPersistence<I> formPersistence) {
        this(id, () -> formPersistence);
    }

    public SingularStudioSimpleCRUDPanel(String id, ISupplier<FormPersistence<I>> formPersistence) {
        super(id);
        this.formPersistence = formPersistence;

        add(container
                .setOutputMarkupId(true)
                .setOutputMarkupPlaceholderTag(true));

        showListContent(null);
    }

    protected FormPersistence<I> getFormPersistence() {
        return this.formPersistence.get();
    }

    protected abstract void buildListTable(BSDataTableBuilder<I, String, IColumn<I, String>> dataTableBuilder);

    private void onEdit(AjaxRequestTarget target, IModel<I> model) {
        showEditContent(target, getFormKey(model.getObject()));
    }

    private void onDelete(AjaxRequestTarget target, IModel<I> model) {
        getFormPersistence().delete(getFormKey(model.getObject()));
        showListContent(target);
    }

    private void onSave(AjaxRequestTarget target, IModel<I> instanceModel) {
        I instance = instanceModel.getObject();
        getFormPersistence().insertOrUpdate(instance, null);
        instanceModel.setObject(getFormPersistence().createInstance());
        showListContent(target);
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

    public static final FormKey getFormKey(SInstance ins) {
        return FormKey.from(ins);
    }

    protected Component newCreateContent(String id) {
        return new FormFragment(id, getFormPersistence().createInstance());
    }

    protected Component newEditContent(String id, FormKey key) {
        return new FormFragment(id, getFormPersistence().load(key));
    }

    protected Component newListContent(String id) {
        return new ListFragment(id);
    }

    private class FormFragment extends Fragment {
        public FormFragment(String id, I instance) {
            super(id, "FormFragment", SingularStudioSimpleCRUDPanel.this);

            Form<?> form = new Form<>("form");
            SingularFormPanel content = new SingularFormPanel(ID_CONTENT, instance);

            add(form
                    .add(content)
                    .add(new ActionAjaxButton("save") {
                        @Override
                        @SuppressWarnings("unchecked")
                        protected void onAction(AjaxRequestTarget target, Form<?> form) {
                            IModel<I> instanceModel = (IModel<I>) content.getInstanceModel();
                            onSave(target, instanceModel);
                        }
                    })
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

            ISortableDataProvider<I, String> dataProvider = new SortableDataProvider<I, String>() {
                @Override
                public Iterator<? extends I> iterator(long first, long count) {
                    return getFormPersistence().loadAll(first, count).iterator();
                }

                @Override
                public long size() {
                    return getFormPersistence().countAll();
                }

                @Override
                public IModel<I> model(I object) {
                    final FormKey key = getFormKey(object);
                    return $m.loadable(object, () -> getFormPersistence().load(key));
                }
            };
            BSDataTableBuilder<I, String, IColumn<I, String>> dataTableBuilder = new BSDataTableBuilder<>(dataProvider);
            buildListTable(dataTableBuilder);
            add(dataTableBuilder
                    .appendActionColumn($m.ofValue("Actions"), col -> col
                            .appendAction($m.ofValue("edit"), Icone.PENCIL, SingularStudioSimpleCRUDPanel.this::onEdit)
                            .appendAction($m.ofValue("delete"), Icone.TRASH, SingularStudioSimpleCRUDPanel.this::onDelete))
                    .build("table"));

            add(new ActionAjaxLink<Void>("create") {
                @Override
                protected void onAction(AjaxRequestTarget target) {
                    showCreateContent(target);
                }
            });
        }
    }
}
