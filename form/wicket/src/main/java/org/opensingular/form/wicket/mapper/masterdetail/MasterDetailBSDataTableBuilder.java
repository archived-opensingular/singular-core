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

import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;

import java.io.Serializable;
import java.util.List;


class MasterDetailBSDataTableBuilder<T, S, PREVCOL extends IColumn<T, S>> extends BSDataTableBuilder<T, S, PREVCOL> implements Serializable {

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
        };
    }
}