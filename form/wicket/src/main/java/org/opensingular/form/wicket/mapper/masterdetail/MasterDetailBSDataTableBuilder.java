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

package org.opensingular.form.wicket.mapper.masterdetail;

import com.google.common.collect.Lists;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.form.wicket.ISValidationFeedbackHandlerListener;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.feedback.FeedbackFence;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BSPaginationPanel;
import org.opensingular.lib.wicket.util.datatable.BSPaginationToolbar;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import java.util.Iterator;
import java.util.List;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;


class MasterDetailBSDataTableBuilder<T, S, PREVCOL extends IColumn<T, S>> extends BSDataTableBuilder<T, S, PREVCOL> {

    MasterDetailBSDataTableBuilder(ISortableDataProvider<T, S> dataProvider) {
        super(dataProvider);
    }

    @Override
    protected BSDataTable<T, S> newDatatable(String id, List<? extends IColumn<T, S>> columns, ISortableDataProvider<T, S> dataProvider) {
        return new BSDataTable<T, S>(id, columns, dataProvider) {
            @Override
            protected AbstractToolbar newNoRecordsToolbar() {
                if (isShowNoRecordsToolbar()) {
                    return new MasterDetailNoRecordsToolbar(this);
                } else {
                    return null;
                }
            }

            @Override
            protected AbstractToolbar newPaginationToolbar() {
                return new BSPaginationToolbar(this) {
                    @Override
                    protected BSPaginationPanel newPagination(String id, DataTable<?, ?> table) {
                        return new BSPaginationPanel(id, table) {
                            @Override
                            protected NumberedPageLink newNumberedPageLink(ListItem<Long> item) {
                                NumberedPageLink link = super.newNumberedPageLink(item);
                                item.add($b.classAppender("has-errors",
                                        $m.get(() -> (!SValidationFeedbackHandler.collectNestedErrors(new FeedbackFence(item)).isEmpty()))));

                                SValidationFeedbackHandler.bindTo(new FeedbackFence(item))
                                        .addListener((handler, target, container, baseInstances, oldErrors, newErrors) -> {
                                            if (target != null)
                                                target.add(item);
                                        })
                                        .setInstanceModels($m.get(() -> {

                                            final IDataProvider<SInstance> dataProvider = (IDataProvider<SInstance>) table.getDataProvider();

                                            final long pageIndex = item.getModelObject();
                                            final long first = pageIndex * table.getItemsPerPage();
                                            final long count = Math.min(table.getItemsPerPage(), dataProvider.size() - first);

                                            final Iterator<? extends SInstance> it = dataProvider.iterator(first, count);
                                            final List<IModel<? extends SInstance>> list = Lists.newArrayList();
                                            while (it.hasNext())
                                                list.add(dataProvider.model(it.next()));
                                            return list;
                                        }));
                                return link;
                            }
                        };
                    }
                };
            }
        };
    }
}