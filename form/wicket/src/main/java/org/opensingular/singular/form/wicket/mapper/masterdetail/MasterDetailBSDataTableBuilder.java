package org.opensingular.singular.form.wicket.mapper.masterdetail;

import org.opensingular.singular.util.wicket.datatable.BSDataTable;
import org.opensingular.singular.util.wicket.datatable.BSDataTableBuilder;
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