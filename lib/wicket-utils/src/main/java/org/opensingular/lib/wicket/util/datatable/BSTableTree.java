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

package org.opensingular.lib.wicket.util.datatable;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.tree.ISortableTreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;

public class BSTableTree<T, S> extends TableTree<T, S> {

    public static final Long DEFAULT_ROWS_PER_PAGE = Long.MAX_VALUE;

    private boolean          stripedRows           = false;
    private boolean          hoverRows             = true;
    private boolean          borderedTable         = true;
    private boolean          condensedTable        = false;

    public BSTableTree(String id, List<? extends IColumn<T, S>> columns, ISortableTreeProvider<T, S> provider) {
        super(id, ensureSerializable(columns), provider, DEFAULT_ROWS_PER_PAGE);

        setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);

        final AbstractToolbar headersToolbar = newHeadersToolbar(provider);
        if (headersToolbar != null) {
            getTable().addTopToolbar(headersToolbar);
        }

        final AbstractToolbar noRecordsToolbar = newNoRecordsToolbar();
        if (noRecordsToolbar != null) {
            getTable().addBottomToolbar(noRecordsToolbar);
        }

        final AbstractToolbar paginationToolbar = newPaginationToolbar();
        if (paginationToolbar != null) {
            getTable().addBottomToolbar(paginationToolbar);
        }

        add(new MetronicTheme());

        getTable().add(new Behavior() {
            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                StringBuilder sbClass = new StringBuilder(StringUtils.defaultString(tag.getAttribute("class")));
                sbClass.append(" table dataTable");
                if (isStripedRows())
                    sbClass.append(" table-striped");
                if (isHoverRows())
                    sbClass.append(" table-hover");
                if (isBorderedTable())
                    sbClass.append(" table-bordered");
                if (isCondensedTable())
                    sbClass.append(" table-condensed");
                tag.put("class", sbClass.toString().trim());
            }

        });
    }

    private static <T extends Serializable> List<T> ensureSerializable(List<T> list) {
        return (list instanceof Serializable) ? list : new ArrayList<>(list);
    }

    protected AbstractToolbar newHeadersToolbar(ISortStateLocator<S> dataProvider) {
        return new BSHeadersToolbar<S>(getTable(), dataProvider);
    }

    protected AbstractToolbar newNoRecordsToolbar() {
        return new NoRecordsToolbar(getTable());
    }

    protected AbstractToolbar newPaginationToolbar() {
        return new BSPaginationToolbar(getTable());
    }

    @Override
    public Component newNodeComponent(String id, IModel<T> model) {
        return super.newNodeComponent(id, model);
    }

    public BSTableTree<T, S> setRowsPerPage(Long rowsPerPage) {
        super.getTable().setItemsPerPage((rowsPerPage != null) ? rowsPerPage : DEFAULT_ROWS_PER_PAGE);
        return this;
    }

    @Override
    protected Component newContentComponent(String id, IModel<T> model) {
        return new Folder<T>(id, this, model) {
            @Override
            protected IModel<?> newLabelModel(IModel<T> model) {
                return $m.ofValue();
            }
        };
    }
    @Override
    protected Item<T> newRowItem(String id, int index, IModel<T> node) {
        return new OddEvenItem<T>(id, index, node);
    }

    public boolean isStripedRows() {
        return stripedRows;
    }
    public BSTableTree<T, S> setStripedRows(boolean stripedRows) {
        this.stripedRows = stripedRows;
        return this;
    }
    public boolean isHoverRows() {
        return hoverRows;
    }
    public BSTableTree<T, S> setHoverRows(boolean hoverRows) {
        this.hoverRows = hoverRows;
        return this;
    }
    public boolean isBorderedTable() {
        return borderedTable;
    }
    public BSTableTree<T, S> setBorderedTable(boolean borderedTable) {
        this.borderedTable = borderedTable;
        return this;
    }
    public boolean isCondensedTable() {
        return condensedTable;
    }
    public BSTableTree<T, S> setCondensedTable(boolean condensedTable) {
        this.condensedTable = condensedTable;
        return this;
    }
}
