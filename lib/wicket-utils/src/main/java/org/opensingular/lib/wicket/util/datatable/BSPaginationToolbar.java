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

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

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
