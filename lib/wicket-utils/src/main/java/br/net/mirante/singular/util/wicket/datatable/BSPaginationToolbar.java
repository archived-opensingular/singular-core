/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.datatable;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class BSPaginationToolbar extends AbstractToolbar {

    private WebMarkupContainer paginator;

    public BSPaginationToolbar(DataTable<?, ?> table) {
        super(table);
        add(paginator = new WebMarkupContainer("paginator"));
        paginator.add(new BSPaginationPanel("pagination", table));
        add($b.addAjaxUpdate(
            new BSItemsPerPageDropDown("itemsPerPage", getTable()),
            (a, c) -> a.add(getTable()))
            .getTargetComponent());
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getTable().getPageCount() > 0);
        paginator.setVisible(getTable().getPageCount() > 1);
    }
}
