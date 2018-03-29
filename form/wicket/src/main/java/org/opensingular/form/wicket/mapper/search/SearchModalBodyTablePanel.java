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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.converter.SimpleSInstanceConverter;
import org.opensingular.form.provider.Config;
import org.opensingular.form.provider.Config.Column;
import org.opensingular.form.provider.ProviderContext;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;
import org.opensingular.lib.wicket.util.datatable.IBSAction;
import org.opensingular.lib.wicket.util.datatable.column.BSActionPanel;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;

@SuppressWarnings("unchecked")
class SearchModalBodyTablePanel extends SearchModalBodyPanel {

    SearchModalBodyTablePanel(String id, WicketBuildContext ctx, IConsumer<AjaxRequestTarget> selectCallback) {
        super(id, ctx, selectCallback);
    }

    public WebMarkupContainer buildResultTable(Config config) {

        final BSDataTableBuilder<Object, ?, ?> builder = new BSDataTableBuilder(new BaseDataProvider() {
            @Override
            public long size() {
                ProviderContext providerContext = new ProviderContext();
                providerContext.setInstance(getCtx().getRootContext().getCurrentInstance());
                providerContext.setFilterInstance(getInnerSingularFormPanel().getInstance());
                return getFilteredProvider().getSize(providerContext);
            }

            @Override
            public Iterator iterator(int first, int count, Object sortProperty, boolean ascending) {
                ProviderContext providerContext = new ProviderContext();
                providerContext.setInstance(getCtx().getRootContext().getCurrentInstance());
                providerContext.setFilterInstance(getInnerSingularFormPanel().getInstance());
                providerContext.setFirst(first);
                providerContext.setCount(count);
                providerContext.setSortProperty(sortProperty);
                providerContext.setAscending(ascending);
                return getFilteredProvider().load(providerContext).iterator();
            }
        });

        builder.setRowsPerPage(getView().getPageSize());

        for (Object o : config.result().getColumns()) {
            configureColumns(builder, (Column) o);
        }

        builder.appendActionColumn(Model.of(), (actionColumn) -> actionColumn
                .appendAction(new BSActionPanel.ActionConfig<>().iconeModel(Model.of(DefaultIcons.ARROW_RIGHT)).titleFunction(m -> "Selecionar"),
                        (IBSAction<Object>) (target, model) ->
                        {
                            SInstanceConverter converter = getInstance().asAtrProvider().getConverter();
                            if (converter == null && !(getInstance() instanceof SIComposite || getInstance() instanceof SIList)) {
                                converter = new SimpleSInstanceConverter<>();
                            }
                            if (converter != null) {
                                converter.fillInstance(getInstance(), (Serializable) model.getObject());
                            }
                            getSelectCallback().accept(target);
                        })
        );

        return builder.build(RESULT_TABLE_ID);
    }

    private void configureColumns(BSDataTableBuilder<Object, ?, ?> builder, Column o) {
        final Column column = o;
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
}