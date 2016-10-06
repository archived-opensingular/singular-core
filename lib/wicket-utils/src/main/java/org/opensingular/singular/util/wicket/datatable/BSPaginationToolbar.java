/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.datatable;

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;

public class BSPaginationToolbar extends AbstractToolbar {

    private WebMarkupContainer paginator;
    private WebMarkupContainer itensPerPageSelector;
    private Long initialRowsPerPage;

    public BSPaginationToolbar(DataTable<?, ?> table) {
        super(table);
        add(paginator = new WebMarkupContainer("paginator"));
        paginator.add(new BSPaginationPanel("pagination", table));
        itensPerPageSelector = $b.addAjaxUpdate(
                new BSItemsPerPageDropDown("itemsPerPage", getTable()),
                (a, c) -> a.add(getTable()))
                .getTargetComponent();
        add(itensPerPageSelector);
    }

    private Long getInitialRowsPerPage() {
        if (initialRowsPerPage == null) {
            initialRowsPerPage = this.getTable().getItemsPerPage();
        }
        return initialRowsPerPage;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        itensPerPageSelector.setVisible(getTable().getItemCount() > getInitialRowsPerPage());
        paginator.setVisible(getTable().getPageCount() > 1);

        /* if at least one control is visible, the toolbar must be visible. if none is visible there is no need for the toolbar.*/
        boolean toolbarVisible = getTable().getPageCount() > 1;
        toolbarVisible |= itensPerPageSelector.isVisible();
        toolbarVisible |= paginator.isVisible();

        this.setVisible(toolbarVisible);
    }
}
