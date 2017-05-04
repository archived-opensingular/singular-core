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

import org.apache.commons.lang3.text.WordUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
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
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;
import org.opensingular.lib.wicket.util.datatable.IBSAction;
import org.opensingular.lib.wicket.util.datatable.column.BSActionPanel;
import org.opensingular.lib.wicket.util.resource.Icone;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;

import static org.opensingular.form.wicket.IWicketComponentMapper.SINGULAR_PROCESS_EVENT;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;

@SuppressWarnings("unchecked")
class SearchModalBodyPanel extends Panel implements Loggable {

    public static final String FILTER_BUTTON_ID = "filterButton";
    public static final String FORM_PANEL_ID    = "formPanel";
    public static final String RESULT_TABLE_ID  = "resultTable";

    private final WicketBuildContext           ctx;
    private final SViewSearchModal             view;
    private final IConsumer<AjaxRequestTarget> selectCallback;

    private SingularFormPanel innerSingularFormPanel;
    private MarkupContainer   resultTable;

    SearchModalBodyPanel(String id, WicketBuildContext ctx, IConsumer<AjaxRequestTarget> selectCallback) {
        super(id);
        this.ctx = ctx;
        this.view = (SViewSearchModal) ctx.getView();
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

        final AjaxButton filterButton;

        innerSingularFormPanel = buildInnerSingularFormPanel();
        filterButton = buildFilterButton();
        resultTable = buildResultTable(getConfig());

        add(innerSingularFormPanel);
        add(filterButton);
        add(resultTable);

        innerSingularFormPanel.add($b.onEnterDelegate(filterButton, SINGULAR_PROCESS_EVENT));

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

    private AjaxButton buildFilterButton() {
        return new AjaxButton(FILTER_BUTTON_ID) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                target.add(resultTable);
            }
        };
    }

    private WebMarkupContainer buildResultTable(Config config) {

        final BSDataTableBuilder<Object, ?, ?> builder = new BSDataTableBuilder(new BaseDataProvider() {
            @Override
            public long size() {
                ProviderContext providerContext = new ProviderContext();
                providerContext.setInstance(ctx.getRootContext().getCurrentInstance());
                providerContext.setFilterInstance(innerSingularFormPanel.getInstance());
                return getFilteredProvider().getSize(providerContext);
            }

            @Override
            public Iterator iterator(int first, int count, Object sortProperty, boolean ascending) {
                ProviderContext providerContext = new ProviderContext();
                providerContext.setInstance(ctx.getRootContext().getCurrentInstance());
                providerContext.setFilterInstance(innerSingularFormPanel.getInstance());
                providerContext.setFirst(first);
                providerContext.setCount(count);
                providerContext.setSortProperty(sortProperty);
                providerContext.setAscending(ascending);
                return getFilteredProvider().load(providerContext).iterator();
            }
        });

        builder.setRowsPerPage(view.getPageSize());

        for (Object o : config.result().getColumns()) {
            final Column column = (Column) o;
            builder.appendPropertyColumn(Model.of(column.getLabel()), object -> {
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
                    throw new SingularFormException("Não foi possivel recuperar a propriedade '" + column.getProperty() + "' via metodo get na classe " + object.getClass());
                }
            });
        }

        builder.appendActionColumn(Model.of(), (actionColumn) -> actionColumn
                .appendAction(new BSActionPanel.ActionConfig<>().iconeModel(Model.of(Icone.HAND_UP)).titleFunction(m -> "Filtrar"),
                        (IBSAction<Object>) (target, model) ->
                        {
                            SInstanceConverter converter = getInstance().asAtrProvider().getConverter();
                            if (converter == null && !(getInstance() instanceof SIComposite || getInstance() instanceof SIList)) {
                                converter = new SimpleSInstanceConverter<>();
                            }
                            if (converter != null) {
                                converter.fillInstance(getInstance(), (Serializable) model.getObject());
                            }
                            selectCallback.accept(target);
                        })
        );

        return builder.build(RESULT_TABLE_ID);
    }

    private SingularFormPanel buildInnerSingularFormPanel() {

        final SingularFormPanel parentSingularFormPanel = this.visitParents(SingularFormPanel.class,
                (parent, visit) -> visit.stop(parent));

        SingularFormPanel p = new SingularFormPanel(FORM_PANEL_ID, true);
        p.setDocumentFactory(parentSingularFormPanel.getDocumentFactory().orElse(null));
        p.setInstanceFromType(RefType.of(() -> getConfig().getFilter()));

        return p;
    }

    private SInstance getInstance() {
        return ctx.getModel().getObject();
    }

}