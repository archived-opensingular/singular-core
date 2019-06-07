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

package org.opensingular.form.wicket.mapper.search;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.converter.SimpleSInstanceConverter;
import org.opensingular.form.document.RefType;
import org.opensingular.form.provider.Config;
import org.opensingular.form.provider.Config.Column;
import org.opensingular.form.provider.FilteredPagedProvider;
import org.opensingular.form.provider.FilteredProvider;
import org.opensingular.form.provider.InMemoryFilteredPagedProviderDecorator;
import org.opensingular.form.provider.ProviderContext;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.form.wicket.model.SInstanceRootModel;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;
import org.opensingular.lib.wicket.util.datatable.IBSAction;
import org.opensingular.lib.wicket.util.datatable.column.BSActionPanel;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.SINGULAR_PROCESS_EVENT;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;

@SuppressWarnings("unchecked")
class SearchModalBodyPanel extends Panel implements Loggable {

    private static final String FILTER_BUTTON_ID         = "filterButton";
    private static final String REMOVE_ELEMENT_BUTTON_ID = "removeElementButton";
    private static final String FORM_PANEL_ID            = "formPanel";
    private static final String RESULT_TABLE_ID          = "resultTable";
    public static final  String FILTRAR                  = "Filtrar";
    public static final  String REMOVER                  = "Remover";

    private final WicketBuildContext          ctx;
    private final ISupplier<SViewSearchModal> viewSupplier;

    @SuppressWarnings("squid:S1068")
    private final IConsumer<AjaxRequestTarget> selectCallback;

    private SingularFormPanel innerSingularFormPanel;
    private DataTableFilter   dataTableFilter;
    private MarkupContainer   resultTable;

    private IModel<Object> selected;

    SearchModalBodyPanel(String id, WicketBuildContext ctx, IConsumer<AjaxRequestTarget> selectCallback) {
        super(id);
        this.ctx            = ctx;
        this.viewSupplier   = ctx.getViewSupplier(SViewSearchModal.class);
        this.selectCallback = selectCallback;
        validate();
    }

    private void validate() {
        if (getInstance().asAtrProvider().getFilteredProvider() == null) {
            throw new SingularFormException("O provider não foi informado", getInstance());
        }
        if (getInstance().asAtrProvider().getConverter() == null
                && (getInstance() instanceof SIComposite || getInstance() instanceof SIList)) {
            throw new SingularFormException("O tipo não é simples e o converter não foi informado.", getInstance());
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final Component filterButton;
        dataTableFilter        = new DataTableFilter();
        innerSingularFormPanel = buildInnerSingularFormPanel();
        filterButton           = buildFilterButton();
        add(buildRemoveElement());
        resultTable = buildResultTable(getConfig());
        resultTable.add(new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                response.render(OnDomReadyHeaderItem.forScript("clickedRow.create();"));
            }
        });

        add(innerSingularFormPanel);
        add(filterButton);
        add(resultTable);

        innerSingularFormPanel.add($b.onEnterDelegate(filterButton, SINGULAR_PROCESS_EVENT));

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(new PackageResourceReference(this.getClass(), "SearchModalBodyPanel.js")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(this.getClass(), "SearchModalBodyPanel.css")));
    }

    private Config getConfig() {
        Config config = new Config();
        getFilteredProvider().configureProvider(config);
        return config;
    }

    private FilteredPagedProvider getFilteredProvider() {
        FilteredProvider provider = getInstance().asAtrProvider().getFilteredProvider();

        if (!(provider instanceof FilteredPagedProvider)) {
            provider = new InMemoryFilteredPagedProviderDecorator<>(provider);
        }
        return (FilteredPagedProvider) provider;
    }

    private Component buildFilterButton() {
        return new AjaxLink<Void>(FILTER_BUTTON_ID) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                SInstance source = innerSingularFormPanel.getInstance();
                SInstance copy = source.getDocument().getDocumentFactoryRef().get()
                        .createInstance(source.getDocument().getRootRefType().orElseThrow(() -> new SingularFormException("Null rootRefType")), false);
                Value.copyValues(source, copy);
                dataTableFilter.setFilter(copy);
                resultTable.setVisible(true);
                target.add(resultTable);
            }
        }.add(new Label("label", Optional.ofNullable(viewSupplier.get().getButtonLabel()).orElse(FILTRAR)));
    }

    private Component buildRemoveElement() {
        return new AjaxLink<Void>(REMOVE_ELEMENT_BUTTON_ID) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                innerSingularFormPanel.getInstance().clearInstance();
                getInstance().clearInstance();
                selected = new Model();
                selectCallback.accept(target);
            }
        }.add(new Label("label", Optional.ofNullable(viewSupplier.get().getButtonLabel()).orElse(REMOVER)))
                .setVisible(this.viewSupplier.get().isShowRemoveButton());
    }

    private WebMarkupContainer buildResultTable(Config config) {

        final BSDataTableBuilder<Object, ?, ?> builder = new BSDataTableBuilder(new BaseDataProvider() {
            @Override
            public long size() {
                ProviderContext providerContext = new ProviderContext();
                providerContext.setInstance(ctx.getRootContext().getCurrentInstance());
                providerContext.setFilterInstance(dataTableFilter.getFilterInstance());
                dataTableFilter.setSize(getFilteredProvider().getSize(providerContext));
                resultTable.setVisible(!(dataTableFilter.isFirstFilter() && dataTableFilter.getSize() == 0));
                dataTableFilter.setFirstFilter(false);
                return dataTableFilter.getSize();

            }

            @Override
            public Iterator iterator(int first, int count, Object sortProperty, boolean ascending) {
                ProviderContext providerContext = new ProviderContext();
                providerContext.setInstance(ctx.getRootContext().getCurrentInstance());
                providerContext.setFilterInstance(dataTableFilter.getFilterInstance());
                providerContext.setFirst(first);
                providerContext.setCount(count);
                providerContext.setSortProperty(sortProperty);
                providerContext.setAscending(ascending);
                dataTableFilter.setElements(getFilteredProvider().load(providerContext));
                return dataTableFilter.getElements().iterator();
            }
        });

        builder.setRowsPerPage(viewSupplier.get().getPageSize());

        for (Object o : config.result().getColumns()) {
            configureColumns(builder, (Column) o);
        }

        builder.appendActionColumn(Model.of(), (actionColumn) -> actionColumn
                .appendAction(new BSActionPanel.ActionConfig<>().iconeModel(Model.of(DefaultIcons.ARROW_RIGHT)).titleFunction(m -> "Selecionar"),
                        (IBSAction<Object>) (target, model) -> {
                            SearchModalBodyPanel.this.selected = model;
                            SInstanceConverter converter = getInstance().asAtrProvider().getConverter();
                            if (converter == null && !(getInstance() instanceof SIComposite || getInstance() instanceof SIList)) {
                                converter = new SimpleSInstanceConverter<>();
                            }
                            if (converter != null) {
                                converter.fillInstance(getInstance(), (Serializable) model.getObject());
                            }
                            selectCallback.accept(target);
                        }));

        final String noRecordsMessage = viewSupplier.get().getNoRecordsMessage();
        if (StringUtils.isNotEmpty(noRecordsMessage)) {
            builder.setNoRecordsMessage(Model.of(noRecordsMessage));
        }

        return builder.build(RESULT_TABLE_ID)
                .setOnNewRowItem(i -> i.add(getSelectedRowBehavior()));
    }

    private Behavior getSelectedRowBehavior() {
        return new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);
                if (component.getDefaultModel().equals(selected)) {
                    component.add($b.classAppender(" selected-item "));
                }
            }
        };
    }

    private void configureColumns(BSDataTableBuilder<Object, ?, ?> builder, Column column) {

        if (viewSupplier.get().isEnableRowClick()) {
            builder.appendPropertyActionColumn(Model.of(column.getLabel()), object -> getCellObject(column, object));
        } else {
            builder.appendPropertyColumn(Model.of(column.getLabel()), object -> getCellObject(column, object));
        }
    }

    private Object getCellObject(Column column, Object object) {
        try {
            if (column.getProperty() != null) {
                final Method getter = object.getClass().getMethod("get" + WordUtils.capitalize(column.getProperty()));
                getter.setAccessible(true);
                return getter.invoke(object);
            } else {
                return object;
            }
        } catch (Exception ex) {
            getLogger().debug(null, ex);
            throw new SingularFormException("Não foi possivel recuperar a propriedade '" + column.getProperty() + "' via metodo get na classe " + object.getClass(), ex.getCause());
        }
    }

    private SingularFormPanel buildInnerSingularFormPanel() {
        SingularFormPanel newSingularFormPanel = new SingularFormPanel(FORM_PANEL_ID, true);
        newSingularFormPanel.setInstanceFromType(RefType.of(() -> getConfig().getFilter()));

        lookParentSingularFormPanel().flatMap(SingularFormPanel::getDocumentFactory)
                .ifPresent(newSingularFormPanel::setDocumentFactory);

        return newSingularFormPanel;
    }

    private Optional<SingularFormPanel> lookParentSingularFormPanel() {
        return Optional.ofNullable(this.visitParents(SingularFormPanel.class, (parent, visit) -> visit.stop(parent)));
    }

    private SInstance getInstance() {
        return ctx.getModel().getObject();
    }

    SInstance getFilterInstance() {
        return innerSingularFormPanel.getInstance();
    }

    private static class DataTableFilter implements Serializable {

        private boolean                         firstFilter   = true; //This represent's the creation of the table.
        private long                            size          = 0L; //The size of the elements of the table.
        private ArrayList                       elements      = new ArrayList(); //All the elements of the table. THIS IS A ArrayList FOR Serializable.
        private ISInstanceAwareModel<SInstance> instanceModel = new SInstanceRootModel<>();

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public List getElements() {
            return elements;
        }

        public void setElements(List elements) {
            this.elements = new ArrayList(elements);
        }

        /**
         * This method returns true just in the first time of the creation of table.
         *
         * @return True if it's the first time of creation table. False otherwise.
         */
        public boolean isFirstFilter() {
            return firstFilter;
        }

        public void setFirstFilter(boolean firstFilter) {
            this.firstFilter = firstFilter;
        }

        public void setFilter(SInstance filterInstance) {
            instanceModel.setObject(filterInstance);
        }

        public SInstance getFilterInstance() {
            return instanceModel.getSInstance();
        }
    }

}