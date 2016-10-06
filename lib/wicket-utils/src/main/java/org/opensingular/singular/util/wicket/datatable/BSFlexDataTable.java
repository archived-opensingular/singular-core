/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.datatable;

import static org.opensingular.singular.util.wicket.util.WicketUtils.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IStyledColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.repeater.IItemFactory;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import org.opensingular.singular.util.wicket.datatable.column.IRowMergeableColumn;

public class BSFlexDataTable<T, S> extends BSDataTable<T, S> {

    private static final MetaDataKey<Integer> ROWSPAN_KEY = new MetaDataKey<Integer>() {
    };

    private transient Object[] lastMergingIds;
    private transient Item<ICellPopulator<T>>[] lastVisibleCellItems;

    public BSFlexDataTable(String id, List<? extends IColumn<T, S>> columns, ISortableDataProvider<T, S> dataProvider) {
        super(id, columns, dataProvider);
        setStripedRows(false);
        setHoverRows(false);
    }

    @Override
    protected DataGridView<T> newDataGridView(String id, List<? extends IColumn<T, S>> columns, IDataProvider<T> dataProvider) {
        return new DefaultDataGridView(id, columns, dataProvider);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        this.lastMergingIds = null;
        this.lastVisibleCellItems = null;
    }

    @SuppressWarnings("unchecked")
    private boolean checkAndSetColumnMergingIdSameAsLast(int columnIndex, IModel<T> currentRowModel) {

        final IColumn<T, S> column = getColumns().get(columnIndex);
        if (!(column instanceof IRowMergeableColumn)) {
            return false;
        }
        final Object lastId = lastMergingIds()[columnIndex];
        final Object currentId = ((IRowMergeableColumn<T>) column).getRowMergeId(currentRowModel);
        if (!Objects.equals(lastId, currentId)) {
            lastMergingIds()[columnIndex] = currentId;
        }

        boolean mergingIdSameAsLast = (lastId != null) && (currentId != null) && Objects.equals(lastId, currentId);
        return mergingIdSameAsLast;
    }

    protected void postPopulateCellItem(Item<ICellPopulator<T>> cellItem, int columnIndex, IModel<T> model) {
        boolean visible = !checkAndSetColumnMergingIdSameAsLast(columnIndex, model);
        cellItem.setVisible(visible);
        if (visible) {
            lastVisibleCellItems()[columnIndex] = cellItem;
            lastVisibleCellItems()[columnIndex].setMetaData(ROWSPAN_KEY, 1);
            cellItem.add($b.attr("rowspan", $m.get(() -> cellItem.getMetaData(ROWSPAN_KEY))));
        } else {
            lastVisibleCellItems()[columnIndex].setMetaData(ROWSPAN_KEY, lastVisibleCellItems()[columnIndex].getMetaData(ROWSPAN_KEY) + 1);
            cellItem.removeAll();
        }
    }

    private Object[] lastMergingIds() {
        if (lastMergingIds == null) {
            lastMergingIds = new Object[getColumns().size()];
        }
        return lastMergingIds;
    }
    @SuppressWarnings("unchecked")
    private Item<ICellPopulator<T>>[] lastVisibleCellItems() {
        if (lastVisibleCellItems == null) {
            lastVisibleCellItems = new Item[getColumns().size()];
        }
        return lastVisibleCellItems;
    }

    private class DefaultDataGridView extends DataGridView<T> {
        public DefaultDataGridView(String id, List<? extends IColumn<T, S>> columns, IDataProvider<T> dataProvider) {
            super(id, columns, dataProvider);
        }

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        protected Item newCellItem(final String id, final int columnIndex, final IModel model) {
            Item item = BSFlexDataTable.this.newCellItem(id, columnIndex, model);
            final IColumn<T, S> column = BSFlexDataTable.this.getColumns().get(columnIndex);
            if (column instanceof IStyledColumn) {
                item.add(new Behavior() {
                    @Override
                    public void onComponentTag(final Component component, final ComponentTag tag) {
                        String className = ((IStyledColumn<T, S>) column).getCssClass();
                        if (!Strings.isEmpty(className)) {
                            tag.append("class", className, " ");
                        }
                    }
                });
            }
            return item;
        }
        @Override
        protected Item<T> newRowItem(final String id, final int index, final IModel<T> model) {
            return BSFlexDataTable.this.newRowItem(id, index, model);
        }
        @Override
        protected void addItems(Iterator<Item<T>> items) {
            super.addItems(items);
        }
        @Override
        protected Iterator<IModel<T>> getItemModels() {
            long offset = getFirstItemOffset();
            long size = getViewSize();
            return new ModelIterator(this.getDataProvider(), offset, size);
        }
        private final class ModelIterator implements Iterator<IModel<T>> {
            private final Iterator<? extends T> items;
            private final IDataProvider<T> dataProvider;
            @SuppressWarnings("unchecked")
            public ModelIterator(IDataProvider<T> dataProvider, long offset, long count) {
                this.dataProvider = dataProvider;
                items = count > 0 ? dataProvider.iterator(offset, count) : (Iterator<? extends T>) Collections.emptyList().iterator();
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            @Override
            public boolean hasNext() {
                return items != null && items.hasNext();
            }
            @Override
            public IModel<T> next() {
                return dataProvider.model(items.next());
            }
        }

        @Override
        protected IItemFactory<T> newItemFactory() {
            return (index, model) -> {
                String id = DefaultDataGridView.this.newChildId();
                Item<T> item = DefaultDataGridView.this.newItem(id, index, model);
                DefaultDataGridView.this.populateItem(item);

                RepeatingView cells = (RepeatingView) item.get("cells");
                Iterator<Component> cellItems = cells.iterator();
                for (int columnIndex = 0; cellItems.hasNext(); columnIndex++) {
                    Item<ICellPopulator<T>> cellItem = (Item<ICellPopulator<T>>) cellItems.next();
                    postPopulateCellItem(cellItem, columnIndex, model);
                }
                return item;
            };
        }
    }
}
